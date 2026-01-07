# Little Code Gems

Most software projects require countless inquiries, considerations and ultimately decisions. Seemingly simple questions can often turn into surprisingly elaborate and deep rabbit holes. These "side quests" may appear insignificant, but overtime add up to a considerable amount of knowledge, expertise and intuition. The lessons learned can become little gems that stay with you long after a particular project. 

## Questions & Answers

### How to connect to H2 with a database browser?

It's not as straightforward as one would hope. H2 memory database is only visible to the JVM process that opens it. Connecting to the seemingly-same database through a browser (example: IntelliJ Database Tool) would not show the expected tables and data. One solution is to configure H2 to store its data to a file and enable auto server mode. Update `application.properties` as follows:

```properties
# IMPORTANT:
# - Don't forget to include ";AUTO_SERVER=true" at the end of url
# - Make sure all directories exist in {PATH_OF_YOUR_CHOICE}. Create them if necessary   
spring.datasource.url=jdbc:h2:file:{PATH_OF_YOUR_CHOICE}/tictactoe;AUTO_SERVER=true
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop

# Optional properties to help monitor SQL queries
spring.jpa.properties.hibernate.show_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true
spring.jpa.properties.hibernate.format_sql=true
```

More Info: [YouTube - Spring Boot Java H2 Database Setup in IntelliJ](https://www.youtube.com/watch?v=8QBJMxyXIqc)

### Why does Spring JPA warn about "spring.jpa.open-in-view is enabled by default" at startup?

In a small project, the warning and its implications can likely be ignored (also you might still be inviting more trouble than it is worth down the road). However, the enabled-by-default setting can have very adverse performance implications in a production environment with high volume of traffic. It is recommended to start new projects with `spring.jpa.open-in-view=false` property in [application.properties](/src/main/resources/application.properties) file.

For a proper and in-depth explanation, see: [The Open Session In View Anti-Pattern](https://vladmihalcea.com/the-open-session-in-view-anti-pattern/). If you are feeling brave, check out the spirited discussion in [Spring Boot Issue #7107](https://github.com/spring-projects/spring-boot/issues/7107).  

### How to disable Hypersistence banner in logs from hibernate-types dependency?

The hibernate-types library is very useful, but logs a large banner at startup. It can be disabled with `hibernate.types.print.banner=false` property in [hibernate-types.properties](/src/main/resources/hibernate-types.properties) file.

More Info: [SO - Disable the Hypersistence banner in Spring Boot](https://stackoverflow.com/questions/61118423/how-to-disable-the-hypersistence-banner-when-using-hibernate-types-52-in-spring)

### Why use AssertJ library in unit tests?

[AssertJ](https://assertj.github.io/doc/) provides a fluent assertion syntax along with lots of built-in conveniences. It tends to result in slightly easier to read code, stronger assertion checks and more user-friendly test failures. 

Examples:

```java
String veryLongMessage = "many words but only one potato ...";
assertThat(veryLongMessage).containsOnlyOnce("potato");

List<String> list = Arrays.asList("b", "c", "a");
assertThat(list).hasSize(3).containsExactlyInAnyOrder("a", "b", "c");

Map<String, String> map = Map.of("potato", "tasty");
assertThat(map).hasSize(1).containsEntry("potato", "tasty");

assertThatThrownBy(() -> someMethodThatThrows())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("out of potatoes");
```

### What is the project's naming convention for unit test methods?

Unit test methods follow the Roy Osherove's naming strategy: [Naming Standards for Unit Tests](https://osherove.com/blog/2005/4/3/naming-standards-for-unit-tests.html). However, your millage may vary and a more recent trend seems to favor the naming strategy outlined by [Vladimir Khorikov](https://enterprisecraftsmanship.com/posts/you-naming-tests-wrong/). 🙃

More Info: [SO - Unit Test Naming Best Practices](https://stackoverflow.com/questions/155436/unit-test-naming-best-practices).

### How to compare enum values in Thymeleaf templates?

In short, it's somewhat clumsy and painful. A couple approaches:
1. Use `T()` operator. Example: `th:if="${state == T(tictactoe.game.entity.Game.GameState).IN_PROGRESS}"`. Rather ugly and verbose due to needing the package path. May cause problems during refactoring.
2. Convert to string `state.name()` before each comparison, but that mostly negates making the enum available to Thymeleaf in the first place. Complicates refactoring as well.
3. Add extra methods to enum itself. Example: `state.isInProgress()`. May not be practical if there are a lot enum values and introduces extra code strictly for sake of Thymeleaf. 

These lackluster options may prompt some to avoid passing enums to the view layer at all if template logic has a lot of comparisons. Rather convert enums to a string in a single place before passing it to the view layer. Hopefully one day Thymeleaf will have a more developer friendly solution.   

More Info: [SO - Comparing Enum Constants in Thymeleaf](https://stackoverflow.com/questions/24937441/comparing-the-enum-constants-in-thymeleaf)
