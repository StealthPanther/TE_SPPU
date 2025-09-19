#include <iostream>
#include <vector>
#include <queue>
#include <unordered_set>
#include <unordered_map>
#include <algorithm>
using namespace std;


void printFrame(const vector<int> &frames, int capacity) {
    for (int i = 0; i < capacity; ++i)
        if (i < frames.size()) cout << frames[i] << " ";
        else cout << "- ";
    cout << endl;
}


void fifo(vector<int> &pages, int capacity) {
    cout << "--- FIFO Page Replacement ---" << endl;
    vector<int> frames;
    queue<int> q;
    unordered_set<int> in_frames;
    int faults = 0;

    for (int page : pages) {
        if (!in_frames.count(page)) {
            faults++;
            if (frames.size() < capacity) {
                frames.push_back(page);
                q.push(page);
                in_frames.insert(page);
            } else {
                int old = q.front(); q.pop();
                q.push(page);
                in_frames.erase(old);
                in_frames.insert(page);
                auto it = find(frames.begin(), frames.end(), old);
                *it = page;
            }
        }
        printFrame(frames, capacity);
    }
    cout << "Total Page Faults (FIFO): " << faults << endl;
}


void lru(vector<int> &pages, int capacity) {
    cout << "--- LRU Page Replacement ---" << endl;
    vector<int> frames;
    unordered_map<int, int> last_used;
    int faults = 0;
    for (int i = 0; i < pages.size(); ++i) {
        int page = pages[i];
        if (find(frames.begin(), frames.end(), page) == frames.end()) {
            faults++;
            if (frames.size() < capacity) {
                frames.push_back(page);
            } else {
                int lru_index = 0, min_time = last_used[frames[0]];
                for (int j = 1; j < frames.size(); ++j) {
                    if (last_used[frames[j]] < min_time) {
                        min_time = last_used[frames[j]];
                        lru_index = j;
                    }
                }
                frames[lru_index] = page;
            }
        }
        last_used[page] = i;
        printFrame(frames, capacity);
    }
    cout << "Total Page Faults (LRU): " << faults << endl;
}


void optimal(vector<int>& pages, int capacity) {
    cout << "--- Optimal Page Replacement ---" << endl;
    vector<int> frames;
    int faults = 0;
    for (int i = 0; i < pages.size(); ++i) {
        int page = pages[i];
        if (find(frames.begin(), frames.end(), page) == frames.end()) {
            faults++;
            if (frames.size() < capacity) {
                frames.push_back(page);
            } else {
                int index_to_replace = -1, farthest = i + 1;
                for (int j = 0; j < frames.size(); ++j) {
                    int next_use = -1;
                    for (int k = i + 1; k < pages.size(); ++k) {
                        if (pages[k] == frames[j]) {
                            next_use = k;
                            break;
                        }
                    }
                    if (next_use == -1) { 
                        index_to_replace = j;
                        break;
                    }
                    if (next_use > farthest) {
                        farthest = next_use;
                        index_to_replace = j;
                    }
                }
                if (index_to_replace == -1)
                    frames[0] = page;
                else
                    frames[index_to_replace] = page;
            }
        }
        printFrame(frames, capacity);
    }
    cout << "Total Page Faults (Optimal): " << faults << endl;
}

int main() {
    int n, capacity;
    cout << "Enter number of pages: ";
    cin >> n;
    cout << "Enter the page reference string: ";
    vector<int> pages(n);
    for (int i = 0; i < n; ++i) cin >> pages[i];
    cout << "Enter number of frames: ";
    cin >> capacity;
    fifo(pages, capacity);
    lru(pages, capacity);
    optimal(pages, capacity);
    return 0;
}
