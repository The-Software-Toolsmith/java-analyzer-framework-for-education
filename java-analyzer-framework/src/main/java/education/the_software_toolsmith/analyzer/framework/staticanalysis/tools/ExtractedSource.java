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


package education.the_software_toolsmith.analyzer.framework.staticanalysis.tools ;

import java.nio.file.Path ;

/**
 * helper class to track where files came from in a zip file
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-16 Initial implementation based on code from ChatGPT 5.2
 * @version 2.0 2025-12-17 first pass quick and dirty mods to handle any adt (nothing changed)
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public record ExtractedSource( Path file,
                               String zipEntryName )
    {}
