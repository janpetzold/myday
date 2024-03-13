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

## Deploy

    mvn azure-functions:deploy

## Missing features/known issues

- [x] Mail with correct domain
- [ ] Proper handling of .env
- [ ] Add sender information
- [ ] Allow multiple recipients
- [ ] Multi-language support
