# Java Analyzer Framework for Education

A Java static and dynamic analysis framework designed specifically for
computer science education, automated grading, and instructional feedback.

fka Java Testing Framework

## Caveat
This project has been evolving for the better part of the last decade.  It has morphed, not always cleanly 
and with no design document as a guide, based upon my needs for any arbitrary lab assignment I gave my students.
Bottom line, it's a bit of a mess but it's time to put it out there.

## Background
Many years ago I had a student who submitted a solution to a programming assignment which passed all 3 automated tests. They earned a zero. Their 'solution' was to check the arguments and return a predetermined answer. A simple, typical way to address this is to test with random data. Yes, for this particular student that would have cleanly identified their work as wrong. Another option was to increase the number of tests - I suspect that for this student it wouldn't have changed anything unless the number of tests increased significantly.

Unfortunately, these approaches wouldn't have done anything to advance their understanding of the subject matter or the skills related to it. They missed the point of the exercise or, more likely, they didn't give themself enough time to properly do the assignment and figured I wouldn't review their code so they just might get away with it - I did, they didn't.

I'm not the only instructor to have encountered this behavior.

I recognized that I could do more to help my students succeed - I could provide them tools to better understand the expected results given a specification.  This started the journey to develop this framework.

At the time, I didn't consider how I might want to enhance the evaluation so I didn't architect a solution. The evolution has therefor been haphazard and challenging - both to implement the framework and to apply it to different assignments. This forced me to make some significant structural changes to the framework - less so to the tests I had already built (backward compatibility is a good thing).

Now, I'm taking a decade of experience with the framework and taking a step back to architect it.

I used JUnit to test the correctness of the results of my students' code.  While a very useful tool in industry, students such as mine (I primarily teach foundation-level courses: CS1, CS2, Data Structures) do not have the experience or insights to effectively use the results of those tests.  These tests also tend to be at a high level, only testing accessible/visible methods; they do not exercise private methods, they don't verify the state of objects, and they require that the entire implementation be in-place.  Generally not a problem for CS1.  Learning object-oriented programming in CS2 introduces new challenges for the students and the tests - properly structured objects can't be interrogated by external code due to encapsulation/visibility constraints.

## The Solution
\[come back soon - I'm workin' on it 8~\]


---

I started this project while teaching at [Wentworth Institute of Technology](https://wit.edu), School of Computing and Data Science.

Copyright © 2016-2026 David M Rosenberg, The Software Toolsmith

## License

This project is licensed under the Apache License, Version 2.0.

See the LICENSE file for details.
