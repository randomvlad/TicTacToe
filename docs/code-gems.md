# Little Code Gems

Most software projects require countless inquiries, considerations and ultimately decisions. Seemingly simple questions can often turn into surprisingly elaborate and deep rabbit holes. These "side quests" may appear insignificant, but overtime add up to a considerable amount of knowledge and expertise. The lessons learned can become little gems that stay with you long after a particular project. 

## Questions & Answers

### How to disable Hypersistence banner in logs from hibernate-types dependency?

Vlad Mihalcea's hibernate-types library is very useful, but logs a large banner at startup. It can be disabled with `hibernate.types.print.banner=false` property in [hibernate-types.properties](/src/main/resources/hibernate-types.properties) file.

See: [SO - Disable the Hypersistence banner in Spring Boot?](https://stackoverflow.com/questions/61118423/how-to-disable-the-hypersistence-banner-when-using-hibernate-types-52-in-spring)
