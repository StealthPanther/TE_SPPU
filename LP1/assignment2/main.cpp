#include <iostream>
#include <thread>
#include <chrono>

using namespace std;

struct Process {
    int pid;
    int arrivalTime;
    int burstTime;
    int remainingTime;
    int startTime;
    int completionTime;
    int waitingTime;
    int turnaroundTime;
    int priority;
    bool completed;
};

void preemptiveFCFS(Process processes[], int n);
void preemptiveSJF(Process processes[], int n);
void roundRobin(Process processes[], int n);
void priorityScheduling(Process processes[], int n);

int main() {
    const int n = 3;

    Process processes[n] = {
        {1, 0, 2, 2, -1, 0, 0, 0, 0, false}, // P1 arrival=0, burst=2
        {2, 5, 3, 3, -1, 0, 0, 0, 0, false}, // P2 arrival=5, burst=3
        {3, 6, 1, 1, -1, 0, 0, 0, 0, false}  // P3 arrival=6, burst=1
    };

    cout << "Select Scheduling Algorithm:\n";
    cout << "1. Preemptive FCFS\n";
    cout << "2. Preemptive SJF (SRTF)\n";
    cout << "3. Round Robin\n";
    cout << "4. Priority Scheduling (Non-preemptive)\n";
    cout << "Enter choice (1-4): ";
    int choice;
    cin >> choice;

    // If Priority Scheduling selected, assign priorities hardcoded (or you can set here)
    if (choice == 4) {
        // Just hardcoding priorities here (you can modify as needed)
        processes[0].priority = 2; // P1 priority 2
        processes[1].priority = 1; // P2 priority 1 (highest)
        processes[2].priority = 3; // P3 priority 3 (lowest)
        cout << "Using hardcoded priorities:\n";
        for (int i = 0; i < n; i++) {
            cout << "Process P" << processes[i].pid << " Priority: " << processes[i].priority << "\n";
        }
    }

    switch (choice) {
        case 1:
            preemptiveFCFS(processes, n);
            break;
        case 2:
            preemptiveSJF(processes, n);
            break;
        case 3:
            roundRobin(processes, n);
            break;
        case 4:
            priorityScheduling(processes, n);
            break;
        default:
            cout << "Invalid choice.\n";
            return 1;
    }

    return 0;
}

// Preemptive FCFS implementation
void preemptiveFCFS(Process processes[], int n) {
    cout << "\n--- Preemptive FCFS ---\n";
    int time = 0, completed = 0;
    int index = 0;

    while (completed < n) {
        while (index < n && processes[index].arrivalTime <= time && processes[index].completed)
            index++;

        if (index < n && processes[index].arrivalTime <= time) {
            Process& current = processes[index];
            if (current.startTime == -1) {
                current.startTime = time;
                cout << "Time " << time << ": P" << current.pid << " started\n";
            }
            current.remainingTime--;
            cout << "Time " << time << ": P" << current.pid << " running\n";

            if (current.remainingTime == 0) {
                current.completed = true;
                current.completionTime = time + 1;
                current.turnaroundTime = current.completionTime - current.arrivalTime;
                current.waitingTime = current.turnaroundTime - current.burstTime;
                cout << "Time " << time + 1 << ": P" << current.pid << " completed\n";
                completed++;
                index++;
            }
        } else {
            cout << "Time " << time << ": CPU IDLE\n";
        }
        std::this_thread::sleep_for(std::chrono::milliseconds(500));
        time++;
    }

    cout << "\nPID\tArrival\tBurst\tStart\tCompletion\tTAT\tWaiting\n";
    for (int i = 0; i < n; i++) {
        cout << "P" << processes[i].pid << "\t" << processes[i].arrivalTime << "\t"
             << processes[i].burstTime << "\t" << processes[i].startTime << "\t"
             << processes[i].completionTime << "\t\t" << processes[i].turnaroundTime << "\t"
             << processes[i].waitingTime << "\n";
    }
}

// Preemptive SJF (Shortest Remaining Time First)
void preemptiveSJF(Process processes[], int n) {
    cout << "\n--- Preemptive SJF (SRTF) ---\n";
    int completed = 0, currentTime = 0;

    while (completed < n) {
        int idx = -1;
        int minRem = 100000;

        for (int i = 0; i < n; i++) {
            if (!processes[i].completed && processes[i].arrivalTime <= currentTime && processes[i].remainingTime < minRem) {
                minRem = processes[i].remainingTime;
                idx = i;
            }
        }

        if (idx == -1) {
            cout << "Time " << currentTime << ": CPU IDLE\n";
            currentTime++;
            std::this_thread::sleep_for(std::chrono::milliseconds(500));
            continue;
        }

        Process& p = processes[idx];
        if (p.startTime == -1) {
            p.startTime = currentTime;
            cout << "Time " << currentTime << ": P" << p.pid << " started\n";
        }

        cout << "Time " << currentTime << ": P" << p.pid << " running\n";
        p.remainingTime--;
        currentTime++;

        if (p.remainingTime == 0) {
            p.completed = true;
            p.completionTime = currentTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
            cout << "Time " << currentTime << ": P" << p.pid << " completed\n";
            completed++;
        }
        std::this_thread::sleep_for(std::chrono::milliseconds(500));
    }

    cout << "\nPID\tArrival\tBurst\tStart\tCompletion\tTAT\tWaiting\n";
    for (int i = 0; i < n; i++) {
        cout << "P" << processes[i].pid << "\t" << processes[i].arrivalTime << "\t"
             << processes[i].burstTime << "\t" << processes[i].startTime << "\t"
             << processes[i].completionTime << "\t\t" << processes[i].turnaroundTime << "\t"
             << processes[i].waitingTime << "\n";
    }
}

// Round Robin Scheduling
void roundRobin(Process processes[], int n) {
    cout << "\n--- Round Robin Scheduling ---\n";
    int timeQuantum;
    cout << "Enter time quantum: ";
    cin >> timeQuantum;

    int time = 0, completed = 0;
    bool inQueue[10] = {false};
    int queue[1000];
    int front = 0, rear = 0;

    // Enqueue processes that have arrived at time 0
    for (int i = 0; i < n; i++) {
        if (processes[i].arrivalTime <= time) {
            queue[rear++] = i;
            inQueue[i] = true;
        }
    }

    while (completed < n) {
        if (front == rear) {
            cout << "Time " << time << ": CPU IDLE\n";
            time++;
            // Check new arrivals
            for (int i = 0; i < n; i++) {
                if (!inQueue[i] && !processes[i].completed && processes[i].arrivalTime <= time) {
                    queue[rear++] = i;
                    inQueue[i] = true;
                }
            }
            std::this_thread::sleep_for(std::chrono::milliseconds(500));
            continue;
        }

        int idx = queue[front++];
        Process& p = processes[idx];

        if (p.startTime == -1) {
            p.startTime = time;
            cout << "Time " << time << ": P" << p.pid << " started\n";
        }

        int execTime = (p.remainingTime < timeQuantum) ? p.remainingTime : timeQuantum;

        for (int t = 0; t < execTime; t++) {
            cout << "Time " << time << ": P" << p.pid << " running\n";
            p.remainingTime--;
            time++;

            // Check new arrivals during execution
            for (int i = 0; i < n; i++) {
                if (!inQueue[i] && !processes[i].completed && processes[i].arrivalTime == time) {
                    queue[rear++] = i;
                    inQueue[i] = true;
                }
            }

            std::this_thread::sleep_for(std::chrono::milliseconds(500));
        }

        if (p.remainingTime == 0) {
            p.completed = true;
            p.completionTime = time;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
            cout << "Time " << time << ": P" << p.pid << " completed\n";
            completed++;
        } else {
            queue[rear++] = idx;  // Requeue the process
        }
    }

    cout << "\nPID\tArrival\tBurst\tStart\tCompletion\tTAT\tWaiting\n";
    for (int i = 0; i < n; i++) {
        cout << "P" << processes[i].pid << "\t" << processes[i].arrivalTime << "\t"
             << processes[i].burstTime << "\t" << processes[i].startTime << "\t"
             << processes[i].completionTime << "\t\t" << processes[i].turnaroundTime << "\t"
             << processes[i].waitingTime << "\n";
    }
}

// Priority Scheduling (Non-preemptive)
void priorityScheduling(Process processes[], int n) {
    cout << "\n--- Priority Scheduling (Non-preemptive) ---\n";

    int completed = 0, currentTime = 0;

    while (completed < n) {
        int idx = -1;
        int highestPriority = 100000;

        for (int i = 0; i < n; i++) {
            if (!processes[i].completed && processes[i].arrivalTime <= currentTime) {
                if (processes[i].priority < highestPriority) {
                    highestPriority = processes[i].priority;
                    idx = i;
                } else if (processes[i].priority == highestPriority) {
                    // Tie breaker - earlier arrival time
                    if (idx != -1 && processes[i].arrivalTime < processes[idx].arrivalTime) {
                        idx = i;
                    }
                }
            }
        }

        if (idx == -1) {
            cout << "Time " << currentTime << ": CPU IDLE\n";
            currentTime++;
            std::this_thread::sleep_for(std::chrono::milliseconds(500));
            continue;
        }

        Process& p = processes[idx];

        if (p.startTime == -1) {
            p.startTime = currentTime;
            cout << "Time " << currentTime << ": P" << p.pid << " started\n";
        }

        currentTime += p.burstTime;
        p.completionTime = currentTime;
        p.turnaroundTime = p.completionTime - p.arrivalTime;
        p.waitingTime = p.turnaroundTime - p.burstTime;
        p.completed = true;

        cout << "Time " << currentTime << ": P" << p.pid << " completed\n";

        completed++;
        std::this_thread::sleep_for(std::chrono::milliseconds(500));
    }

    cout << "\nPID\tArrival\tBurst\tPriority\tStart\tCompletion\tTAT\tWaiting\n";
    for (int i = 0; i < n; i++) {
        cout << "P" << processes[i].pid << "\t" << processes[i].arrivalTime << "\t"
             << processes[i].burstTime << "\t" << processes[i].priority << "\t\t"
             << processes[i].startTime << "\t" << processes[i].completionTime << "\t\t"
             << processes[i].turnaroundTime << "\t" << processes[i].waitingTime << "\n";
    }
}
