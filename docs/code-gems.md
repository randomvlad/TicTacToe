# Little Code Gems

Most software projects require countless inquiries, considerations and ultimately decisions. Seemingly simple questions can often turn into surprisingly elaborate and deep rabbit holes. These "side quests" may appear insignificant, but overtime add up to a considerable amount of knowledge and expertise. The lessons learned can become little gems that stay with you long after a particular project. 

## Questions & Answers

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

Vlad Mihalcea's hibernate-types library is very useful, but logs a large banner at startup. It can be disabled with `hibernate.types.print.banner=false` property in [hibernate-types.properties](/src/main/resources/hibernate-types.properties) file.

More Info: [SO - Disable the Hypersistence banner in Spring Boot](https://stackoverflow.com/questions/61118423/how-to-disable-the-hypersistence-banner-when-using-hibernate-types-52-in-spring)
