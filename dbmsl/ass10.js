// --- Setup: example collection and sample data ---
db.createCollection("Students"); // if not already created

db.Students.insertMany([
  { name: "Manaswa", age: 21, branch: "CE", grade: "A+", email: "manaswa@example.com" },
  { name: "Kaushal", age: 21, branch: "ECE", grade: "A", email: "kaushal@example.com" },
  { name: "Piyush", age: 22, branch: "CSE", grade: "B", email: "piyush@example.com" },
  { name: "Asha", age: 20, branch: "CSE", grade: "A+", email: "asha@example.com" },
  { name: "Rohit", age: 22, branch: "ECE", grade: "B", email: "rohit@example.com" }
]);


// --- Aggregation Queries ---

// 1. Single aggregation: count students per branch
db.Students.aggregate([
  { $group: { _id: "$branch", count: { $sum: 1 } } },
  { $sort: { count: -1 } }
]);

// 2. Aggregation pipeline 1: count per branch with filter, project and sort (4 stages)
db.Students.aggregate([
  { $group: { _id: "$branch", count: { $sum: 1 } } },      // Group + count
  { $match: { count: { $gte: 2 } } },                      // Filter count ≥ 2
  { $project: { branch: "$_id", count: 1, _id: 0 } },      // Project output fields
  { $sort: { count: -1 } }                                 // Sort descending
]);

// 3. Aggregation pipeline 2: average age per grade with match, project, limit (4 stages)
db.Students.aggregate([
  { $group: { _id: "$grade", avgAge: { $avg: "$age" } } }, // Group + avg
  { $match: { avgAge: { $gte: 21 } } },                    // Filter avgAge ≥ 21
  { $project: { grade: "$_id", avgAge: "$avgAge", _id: 0 } }, // Project, round avgAge
  { $limit: 3 }                                            // Limit result count
]);

// --- Indexes ---

// Single index on name (ascending)
db.Students.createIndex({ name: 1 });

// Compound text index on name and branch
db.Students.createIndex({ name: "text", branch: "text" });

// Unique index on email field
db.Students.createIndex({ email: 1 }, { unique: true });

// Show all indexes
db.Students.getIndexes();

// --- Example Queries with explain("executionStats") ---

// Query using single index on name
db.Students.find({ name: "Kaushal" }).explain("executionStats");

// Query using compound text index
db.Students.find({ $text: { $search: "Kaushal" } }).explain("executionStats");

// --- Drop indexes ---

// Drop by key spec
db.Students.dropIndex({ name: 1 });

// Or drop by index name
db.Students.dropIndex("name_1");
