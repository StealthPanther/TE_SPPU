// Create Students collection and insert 25 documents
db.createCollection("Students");

db.Students.insertMany([
  { name: "Niraj", age: 21, branch: "CSE", grade: "A" },
  { name: "Manaswa", age: 21, branch: "CE", grade: "A+" },
  { name: "Kaushal", age: 21, branch: "ECE", grade: "A" },
  { name: "Piyush", age: 22, branch: "CSE", grade: "B" },
  { name: "Asha", age: 20, branch: "CSE", grade: "A+" },
  { name: "Rohit", age: 22, branch: "ECE", grade: "B" },
  { name: "Sonal", age: 19, branch: "ME", grade: "B" },
  { name: "Raj", age: 23, branch: "CSE", grade: "A" },
  { name: "Kavita", age: 20, branch: "CE", grade: "A" },
  { name: "Sunil", age: 21, branch: "ECE", grade: "B+" },
  { name: "Neha", age: 22, branch: "CSE", grade: "A" },
  { name: "Amit", age: 23, branch: "ME", grade: "B+" },
  { name: "Priya", age: 20, branch: "CE", grade: "A" },
  { name: "Vikas", age: 21, branch: "ECE", grade: "B" },
  { name: "Alok", age: 22, branch: "ME", grade: "A-" },
  { name: "Rina", age: 20, branch: "CSE", grade: "A+" },
  { name: "Kiran", age: 21, branch: "ECE", grade: "A" },
  { name: "Mansi", age: 22, branch: "CE", grade: "B+" },
  { name: "Harsh", age: 23, branch: "ME", grade: "A" },
  { name: "Rashmi", age: 20, branch: "CSE", grade: "A" },
  { name: "Jay", age: 21, branch: "ECE", grade: "B+" },
  { name: "Sakshi", age: 22, branch: "CE", grade: "A-" },
  { name: "Deepak", age: 23, branch: "ME", grade: "B+" },
  { name: "Anita", age: 20, branch: "CSE", grade: "A" },
  { name: "Rohini", age: 21, branch: "ECE", grade: "B" }
]);

// Map function to emit branch and {count:1, totalAge: age} object
var mapFunction = function () {
  emit(this.branch, { count: 1, totalAge: this.age });
};

// Reduce function to sum counts and total ages per branch
var reduceFunction = function (key, values) {
  var result = { count: 0, totalAge: 0 };
  values.forEach(function (value) {
    result.count += value.count;
    result.totalAge += value.totalAge;
  });
  return result;
};

// Finalize function to compute average age per branch after reduction
var finalizeFunction = function (key, value) {
  value.avgAge = value.totalAge / value.count;
  return value;
};

// Run MapReduce with finalize to get count and average age per branch
db.Students.mapReduce(
  mapFunction,
  reduceFunction,
  { out: "branch_stats", finalize: finalizeFunction }
);

// Query the results
print("Branch stats with count and average age:");
db.branch_stats.find().pretty();
