// Preset tag options
export const PRESET_TAGS = [
  // Data Structures
  { label: "Array", value: "Array" },
  { label: "Linked List", value: "Linked List" },
  { label: "Stack", value: "Stack" },
  { label: "Queue", value: "Queue" },
  { label: "Tree", value: "Tree" },
  { label: "Binary Tree", value: "Binary Tree" },
  { label: "Binary Search Tree", value: "Binary Search Tree" },
  { label: "Heap", value: "Heap" },
  { label: "Graph", value: "Graph" },
  { label: "Hash Table", value: "Hash Table" },
  { label: "String", value: "String" },
  { label: "Matrix", value: "Matrix" },

  // Algorithms
  { label: "Sorting", value: "Sorting" },
  { label: "Search", value: "Search" },
  { label: "Binary Search", value: "Binary Search" },
  { label: "Two Pointers", value: "Two Pointers" },
  { label: "Sliding Window", value: "Sliding Window" },
  { label: "Dynamic Programming", value: "Dynamic Programming" },
  { label: "Greedy", value: "Greedy" },
  { label: "Backtracking", value: "Backtracking" },
  { label: "Divide and Conquer", value: "Divide and Conquer" },
  { label: "Recursion", value: "Recursion" },
  { label: "Iteration", value: "Iteration" },
  { label: "Depth-First Search", value: "Depth-First Search" },
  { label: "Breadth-First Search", value: "Breadth-First Search" },
  { label: "Union Find", value: "Union Find" },
  { label: "Topological Sort", value: "Topological Sort" },
  { label: "Shortest Path", value: "Shortest Path" },
  { label: "Minimum Spanning Tree", value: "Minimum Spanning Tree" },

  // Difficulty
  { label: "Easy", value: "Easy" },
  { label: "Medium", value: "Medium" },
  { label: "Hard", value: "Hard" },

  // Programming Languages
  { label: "Java", value: "Java" },
  { label: "Python", value: "Python" },
  { label: "C++", value: "C++" },
  { label: "JavaScript", value: "JavaScript" },
  { label: "Go", value: "Go" },
  { label: "Rust", value: "Rust" },

  // Others
  { label: "Math", value: "Math" },
  { label: "Bit Manipulation", value: "Bit Manipulation" },
  { label: "Simulation", value: "Simulation" },
  { label: "Design", value: "Design" },
  { label: "Database", value: "Database" },
  { label: "System Design", value: "System Design" },
  { label: "Multithreading", value: "Multithreading" },
  { label: "Network", value: "Network" },
  { label: "Security", value: "Security" },
  { label: "Machine Learning", value: "Machine Learning" },
  { label: "Artificial Intelligence", value: "Artificial Intelligence" },
];

// Get tag options
export const getTagOptions = () => {
  return PRESET_TAGS;
};

// Get tag object by tag value
export const getTagByValue = (value: string) => {
  return PRESET_TAGS.find((tag) => tag.value === value);
};

// Get tag objects array by tag values array
export const getTagsByValues = (values: string[]) => {
  return values.map((value) => getTagByValue(value)).filter(Boolean);
};
