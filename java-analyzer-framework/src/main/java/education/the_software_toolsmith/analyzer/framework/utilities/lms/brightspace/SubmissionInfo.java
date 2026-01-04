/* @formatter:off
 *
 * Copyright © 2016-2025 David M Rosenberg, The Software Toolsmith
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @formatter:on
 */


package education.the_software_toolsmith.analyzer.framework.utilities.lms.brightspace ;

import java.nio.file.Path ;
import java.time.LocalDateTime ;

/**
 * contains the information from parsing a Brightspace submission folder name
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-21 Initial implementation based on code from ChatGPT 5.2
 *
 * @param courseUserId
 *     unique user id for this course, either of a student or a group
 * @param assignmentId
 *     unique assignment id
 * @param groupId
 *     for a group project contains the group name, otherwise null
 * @param submitterName
 *     name (First Last) of the individual who made this submission
 * @param submittedAt
 *     date/time of the submission
 * @param folder
 *     path to the submission folder
 */
public record SubmissionInfo( String courseUserId,
                              String assignmentId,
                              String groupId,
                              String submitterName,
                              LocalDateTime submittedAt,
                              Path folder )
    {}   // end record SubmissionInfo
