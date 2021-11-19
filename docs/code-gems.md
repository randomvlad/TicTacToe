# Little Code Gems

Most software projects require countless inquiries, considerations and ultimately decisions. Seemingly simple questions can often turn into surprisingly elaborate and deep rabbit holes. These "side quests" may appear insignificant, but overtime add up to a considerable amount of knowledge and expertise. The lessons learned can become little gems that stay with you long after a particular project. 

## Questions & Answers

### Why does Spring JPA warn about "spring.jpa.open-in-view is enabled by default" at startup?

In a small project, the warning and its implications can likely be ignored (also you might still be inviting more trouble than it is worth down the road). However, the enabled-by-default setting can have very adverse performance implications in a production environment with high volume of traffic. It is recommended to start new projects with `spring.jpa.open-in-view=false` property in [application.properties](/src/main/resources/application.properties) file.

For a proper and in-depth explanation, see: [The Open Session In View Anti-Pattern](https://vladmihalcea.com/the-open-session-in-view-anti-pattern/). If you are feeling brave, check out the spirited discussion in [Spring Boot Issue #7107](https://github.com/spring-projects/spring-boot/issues/7107).  

### What's a convenient way to map JSON to a database table column?

Tic Tac Toe's board data is stored as JSON in table column `game.rows`. A common approach to achieve such a mapping can be either through custom serialization methods or an attribute converter (examples: [Baeldung - Persist a JSON Object Using Hibernate](https://www.baeldung.com/hibernate-persist-json-object)). However, neither is particularly convenient and requires extra code. An annotation driven solution can be more elegant and compact, and that's exactly what [Vlad Mihalcea's hibernate-types](https://mvnrepository.com/artifact/com.vladmihalcea/hibernate-types-52/2.14.0) library provides.

Example:
```java
@TypeDef(name = "json", typeClass = JsonType.class)
public class Game {

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private List<List<String>> rows;

}
```

More Info: [SO - Map JSON column with JPA and Hibernate](https://stackoverflow.com/questions/39620317/how-to-map-a-json-column-with-h2-jpa-and-hibernate)

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
