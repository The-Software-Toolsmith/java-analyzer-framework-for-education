MethodDiffInspector helper classes
==================================

This folder contains small helper classes that use JavaParser to:
- Compare starter vs. student Java source at the method level
- Identify which methods changed
- Extract the student's original method source (not pretty-printed)
- Heuristically detect use of toArray() and Node-based traversal
- Produce a human-readable report and a compliance flag that can be
  consumed by your testing framework.

Classes:
- MethodKey.java
- MethodCollector.java
- MethodNormalizer.java
- OriginalSource.java
- Heuristics.java
- NodeTraversalDetector.java
- MethodDiffInspector.java

Usage sketch:

    Path starter = Path.of("StarterList.java");
    Path student = Path.of("StudentList.java");

    List<String> mustWalkNode = List.of("copyFrom", "insertAll");

    MethodDiffInspector.Result r =
            MethodDiffInspector.analyze(starter, student, mustWalkNode);

    System.out.println(r.report);

    if (!r.compliant) {
        // e.g., zero out all behavioral test scores in your framework
        System.out.println("Structure non-compliant; zeroing test scores.");
    }

You must add JavaParser to your project, for example via Maven:

    <dependency>
        <groupId>com.github.javaparser</groupId>
        <artifactId>javaparser-core</artifactId>
        <version>3.26.2</version>
    </dependency>

Adjust the NodeTraversalDetector heuristic and the list of
requiredNodeTraversalMethods to match each assignment's needs.
