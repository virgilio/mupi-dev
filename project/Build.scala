import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {
    val appName         = "mupi"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "be.objectify"              %% "deadbolt-2"                % "1.1.3-SNAPSHOT",
      "com.feth"                  %% "play-authenticate"         % "0.2.0-SNAPSHOT",
      "postgresql"                %  "postgresql"                % "9.1-901.jdbc4",
      "mysql"                     %  "mysql-connector-java"      % "5.1.18",
      "com.typesafe"              %% "play-plugins-mailer"       % "2.0.4",
      "commons-io"                %  "commons-io"                % "2.3",
      "commons-collections"       %  "commons-collections"       % "3.0",
      "org.jsoup"                 %  "jsoup"                     % "1.7.1",
      "commons-validator"         %  "commons-validator"         % "1.3.1",
      "oro"                       %  "oro"                       % "2.0.8"
    )
    
     
//  Uncomment this for local development of the Play Authenticate core:
/*
    val playAuthenticate = PlayProject(
      "play-authenticate", "1.0-SNAPSHOT", mainLang = JAVA, path = file("modules/play-authenticate")
    ).settings(
      libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.2",
      libraryDependencies += "com.feth" %% "play-easymail" % "0.1-SNAPSHOT",
      libraryDependencies += "org.mindrot" % "jbcrypt" % "0.3m",
      libraryDependencies += "commons-lang" % "commons-lang" % "2.6",

      resolvers += Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns)
    )
*/

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      resolvers += Resolver.url("Objectify Play Repository", url("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("Objectify Play Repository", url("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns),

      resolvers += Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns),

      resolvers += Resolver.url("play-authenticate (release)", url("http://joscha.github.com/play-authenticate/repo/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("play-authenticate (snapshot)", url("http://joscha.github.com/play-authenticate/repo/snapshots/"))(Resolver.ivyStylePatterns)
      
//      resolvers += "Owasp Java-html Sanitizer Repository" at "http://owasp-java-html-sanitizer.googlecode.com/svn/maven/"
    )

}

 
