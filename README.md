# mydaygpt
Source code for my GPT that enables sending out appointments easily via mail

## Setup
Baseline: https://learn.microsoft.com/en-us/azure/azure-functions/create-first-function-cli-java

mvn archetype:generate -DarchetypeGroupId=com.microsoft.azure -DarchetypeArtifactId=azure-functions-archetype -DjavaVersion=17

Java 17, Maven, Azure Function Core Tools

Needs .env file with API key

## Compile

    mvn clean package -DskipTests=true

## Run locally

    mvn azure-functions:run

Note - this may take some time in case of updates of the function tools download some update. This is not indicated in the command-line.

## Deploy

    mvn clean package -P production -DskipTests=true 
    mvn azure-functions:deploy -P production

## Missing features/known issues

- [x] Mail with correct domain
- [ ] Proper handling of .env
- [ ] Add logging in classes other than Function.java
- [ ] Add function testing, e.g. for malformed JSON and mandatory/optional attributes
- [x] Fix .env deploy issue
- [x] Add sender and appointment title information
- [ ] Add support for alarms (http://ical4j.github.io/docs/ical4j/api/3.0.0/net/fortuna/ical4j/model/component/VAlarm.html)
- [ ] Split e-mail creation and actual sending for better testing
- [ ] Improve mail attachment file name
- [ ] Display time in user-friendly format depending on the locale in mail body
- [ ] Allow multiple recipients
- [ ] Multi-language support (correct language should be auto-determined)
- [ ] Write proper README
- [ ] Add another API for date handling like "tomorrow", "Sunday" etc.
- [ ] Add host verification
- [ ] Create cost saving alert on Azure
