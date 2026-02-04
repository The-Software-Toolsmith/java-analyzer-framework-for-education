CheckstyleTester helper
=======================

This small helper class lets you run Checkstyle programmatically against one or
more .java files and consume the results inside your own grading / testing tools.

Files
-----

- CheckstyleTester.java
- checkstyle.xml  (placeholder - replace with your own ruleset)
- README.txt

Dependency
----------

You must add Checkstyle to your project, for example via Maven:

    <dependency>
        <groupId>com.puppycrawl.tools</groupId>
        <artifactId>checkstyle</artifactId>
        <version>10.17.0</version>
    </dependency>

Usage sketch
------------

    import java.nio.file.Path;
    import java.util.List;

    Path style = Path.of("checkstyle.xml");

    List<Path> sources = List.of(
        Path.of("StudentList.java"),
        Path.of("Helper.java")
    );

    CheckstyleTester.Result r =
            CheckstyleTester.runCheckstyle(style, sources);

    System.out.println(r.report);

    if (!r.isCompliant) {
        // e.g., zero out all behavioral test scores in your framework
        System.out.println("Checkstyle violations detected; zeroing test scores.");
    }

You can integrate this with your existing MethodDiffInspector-based structure
checks and your custom testing framework as a "style pre-check" step before
running behavioral tests.
