// Write a program to solve Classical Problems of Synchronization
// using Mutex and Semaphore


#include <iostream>
#include <thread>
#include <mutex>
#include <semaphore.h>
#include <queue>
#include <chrono>
#include <unistd.h>

using namespace std;

queue<int>buffer;
const int MAX_BUFFER_SIZE =3;

mutex mtx;

sem_t emptyslots;
sem_t fullslots;

void producer(){
    int item =1;
    while (true){
        sem_wait(&emptyslots);
        mtx.lock();

        buffer.push(item);
        cout << "Produced: " << item << endl;
        item++;
        mtx.unlock();
        sem_post(&fullslots);

        this_thread::sleep_for(chrono::seconds(2));

    }
}

void consumer(){
    while(true){
        sem_wait(&fullslots);
        mtx.lock();

        int item =buffer.front();
        buffer.pop();
        cout << "Consumed: " << item << endl;

        mtx.unlock();

        sem_post(&emptyslots);
        this_thread::sleep_for(chrono::seconds(4));
        

    }
}

int main(){

    sem_init(&emptyslots,0,MAX_BUFFER_SIZE);
    sem_init(&fullslots,0,0);

    thread prod(producer);
    thread cons(consumer);

    prod.join();
    cons.join();

    sem_destroy(&fullslots);
    sem_destroy(&emptyslots);

    return 0;
}



// g++ -std=c++11 -pthread lp1.cpp -o lp1 -lrt
//./lp1
