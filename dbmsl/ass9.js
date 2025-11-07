// Create collection
db.createCollection("Students");

// Insert one document with embedded array field
db.Students.insertOne({ 
  name: "Niraj", 
  age: 21, 
  branch: "CSE", 
  grade: "A",
  subjects: [
    { name: "Math", score: 95 },
    { name: "Physics", score: 90 }
  ]
});

// Insert multiple documents with embedded array field
db.Students.insertMany([
  { 
    name: "Manaswa", 
    age: 21, 
    branch: "CE", 
    grade: "A+", 
    subjects: [
      { name: "Math", score: 88 },
      { name: "Chemistry", score: 92 }
    ]
  },
  { 
    name: "Kaushal", 
    age: 21, 
    branch: "ECE", 
    grade: "A",
    subjects: [
      { name: "Physics", score: 85 },
      { name: "Electronics", score: 90 }
    ]
  },
]);

// Read all students
db.Students.find();

// Query using logical operators on top-level fields
db.Students.find({ $and: [{ branch: "CSE" }, { grade: "A" }] });
db.Students.find({ $or: [{ branch: "CSE" }, { grade: "A" }] });

// Query using $elemMatch on embedded array field (`subjects`)
db.Students.find({
  subjects: { $elemMatch: { name: "Math", score: { $gt: 90 } } }
});

// Update score of a subject using positional operator $
db.Students.updateOne(
  { name: "Niraj", "subjects.name": "Physics" },
  { $set: { "subjects.$.score": 92 } }
);+

// Update status for all students belonging to a branch
db.Students.updateMany({ branch: "CSE" }, { $set: { status: "active" } });

// Delete one student by name
db.Students.deleteOne({ name: "Piyush" });

// Delete all students in a branch
db.Students.deleteMany({ branch: "ECE" });




//===================================================

// Create Movie collection
db.createCollection("Movies");

// Insert ONE movie with cast array and embedded reviews
db.Movies.insertOne({
  title: "Inception",
  year: 2010,
  genre: "Sci-Fi",

  // Cast array
  cast: ["Leonardo DiCaprio", "Tom Hardy", "Elliot Page"],

  // Embedded reviews array
  reviews: [
    { user: "Kaushal", rating: 5, comment: "Mind-blowing movie!" },
    { user: "Niraj", rating: 4, comment: "Great concept, slightly confusing." }
  ]
});

// Insert MANY movies
db.Movies.insertMany([
  {
    title: "Interstellar",
    year: 2014,
    genre: "Sci-Fi",
    cast: ["Matthew McConaughey", "Anne Hathaway"],
    reviews: [
      { user: "Manaswa", rating: 5, comment: "Masterpiece!" }
    ]
  },
  {
    title: "KGF",
    year: 2018,
    genre: "Action",
    cast: ["Yash", "Srinidhi Shetty"],
    reviews: [
      { user: "Rutuja", rating: 4, comment: "Power packed movie!" }
    ]
  }
]);

// Read ALL movies
db.Movies.find();

// Simple queries using AND / OR
db.Movies.find({ $and: [{ genre: "Sci-Fi" }, { year: { $gt: 2000 } }] });
db.Movies.find({ $or: [{ genre: "Action" }, { year: 2010 }] });

// Query using $elemMatch on embedded reviews
db.Movies.find({
  reviews: { $elemMatch: { rating: { $gt: 4 } } }
});

// Update review rating using positional operator $
db.Movies.updateOne(
  { title: "Inception", "reviews.user": "Niraj" },
  { $set: { "reviews.$.rating": 5 } }
);

// Add a new field to all Action movies
db.Movies.updateMany(
  { genre: "Action" },
  { $set: { status: "blockbuster" } }
);

// Delete one movie by title
db.Movies.deleteOne({ title: "KGF" });

// Delete all Sci-Fi movies
db.Movies.deleteMany({ genre: "Sci-Fi" });
