    #include <iostream>
    #include <vector>
    using namespace std;

    void firstFit(vector<int> blocks, vector<int> processes)
    {
        vector<int> allocation(processes.size(), -1);
        for (int i = 0; i < processes.size(); i++)
        {
            for (int j = 0; j < blocks.size(); j++)
            {
                if (blocks[j] >= processes[i])
                {
                    allocation[i] = j;
                    blocks[j] -= processes[i];
                    break;
                }
            }
        }
        cout << "Process No\tProcess Size\tBlock No\n";
        for (int i = 0; i < processes.size(); i++)
            if (allocation[i] != -1)
                cout << i + 1 << "\t\t" << processes[i] << "\t\t" << allocation[i] + 1 << endl;
            else
                cout << i + 1 << "\t\t" << processes[i] << "\t\tNot Allocated\n";
    }









    
    void bestFit(vector<int> blocks, vector<int> processes)
    {
        vector<int> allocation(processes.size(), -1);
        for (int i = 0; i < processes.size(); i++)
        {
            int bestIdx = -1;
            for (int j = 0; j < blocks.size(); j++)
            {
                if (blocks[j] >= processes[i])
                {
                    if (bestIdx == -1 || blocks[j] < blocks[bestIdx])
                        bestIdx = j;
                }
            }
            if (bestIdx != -1)
            {
                allocation[i] = bestIdx;
                blocks[bestIdx] -= processes[i];
            }
        }
        cout << "Process No\tProcess Size\tBlock No\n";
        for (int i = 0; i < processes.size(); i++)
            if (allocation[i] != -1)
                cout << i + 1 << "\t\t" << processes[i] << "\t\t" << allocation[i] + 1 << endl;
            else
                cout << i + 1 << "\t\t" << processes[i] << "\t\tNot Allocated\n";
    }

    void worstFit(vector<int> blocks, vector<int> processes)
    {
        vector<int> allocation(processes.size(), -1);
        for (int i = 0; i < processes.size(); i++)
        {
            int worstIdx = -1;
            for (int j = 0; j < blocks.size(); j++)
            {
                if (blocks[j] >= processes[i])
                {
                    if (worstIdx == -1 || blocks[j] > blocks[worstIdx])
                        worstIdx = j;
                }
            }
            if (worstIdx != -1)
            {
                allocation[i] = worstIdx;
                blocks[worstIdx] -= processes[i];
            }
        }
        cout << "Process No\tProcess Size\tBlock No\n";
        for (int i = 0; i < processes.size(); i++)
            if (allocation[i] != -1)
                cout << i + 1 << "\t\t" << processes[i] << "\t\t" << allocation[i] + 1 << endl;
            else
                cout << i + 1 << "\t\t" << processes[i] << "\t\tNot Allocated\n";
    }

    void nextFit(vector<int> blocks, vector<int> processes)
    {
        vector<int> allocation(processes.size(), -1);
        int j = 0; // position where the last search ended
        for (int i = 0; i < processes.size(); i++)
        {
            int count = 0;
            bool allocated = false;
            while (count < blocks.size())
            {
                if (blocks[j] >= processes[i])
                {
                    allocation[i] = j;
                    blocks[j] -= processes[i];
                    allocated = true;
                    break;
                }
                j = (j + 1) % blocks.size();
                count++;
            }
            if (allocated)
                j = (j + 1) % blocks.size(); // Start next search from next block
        }
        cout << "Process No\tProcess Size\tBlock No\n";
        for (int i = 0; i < processes.size(); i++)
            if (allocation[i] != -1)
                cout << i + 1 << "\t\t" << processes[i] << "\t\t" << allocation[i] + 1 << endl;
            else
                cout << i + 1 << "\t\t" << processes[i] << "\t\tNot Allocated\n";
    }

    int main()
    {
        int nb, np, option;
        cout << "Enter number of memory blocks: ";
        cin >> nb;
        cout << "Enter number of processes: ";
        cin >> np;
        vector<int> blocks(nb), processes(np);
        cout << "Enter block sizes:\n";
        for (int i = 0; i < nb; i++)
            cin >> blocks[i];
        cout << "Enter process sizes:\n";
        for (int i = 0; i < np; i++)
            cin >> processes[i];
        while (true)
        
        {
            cout << "Choose placement strategy:\n";
            cout << "1. First Fit\n2. Best Fit\n3. Worst Fit\n4. Next Fit\n5. EXIT";
            cout << "Enter option: ";
            cin >> option;

            vector<int> blockcopy = blocks;

            switch (option)
            {
            case 1:
                firstFit(blockcopy, processes);
                break;
            case 2:
                bestFit(blockcopy, processes);
                break;
            case 3:
                worstFit(blockcopy, processes);
                break;
            case 4:
                nextFit(blockcopy, processes);
                break;
            case 5:
                cout << "exited" << endl;
                return 0;
            default:
                cout << "Invalid option.\n";
            }
        }
    }


    // Memory Blocks : 5
    // Number of Processes : 4

    // Block Sizes : 100 500 200 300 600

    // Process Sizes : 210 420 105 400
