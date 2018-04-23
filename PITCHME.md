# How to Mock a SMTP Service
## What is SMTP
## How it works
## How to Mock a SMTP

### Approach 1 - _Using a Fake SMTP_
What do you need or what should you look for?. This is the minimum that you should have to mock a **SMTP** service and some times, it would be the only option.
- Basic - SMTP protocol :) obvoiusly.
- Highly Desirable - Service API.
- Desirable - SDK for your target language.


- **fake-smtp-server nodejs** 
    - Accomplishes with the first two
- FakeSMTP - java
    - Accomplishes only the first one.
- Drawback
    - Test would depent on an external service
    - Either additional code would be needed to consume the API or manual verification.
- Pros
    - The mayority of options are here.
    - Indepency of the test programming language.

Quick exmple: Using fake-smtp-server nodejs

### Approach 2 - Using embebed SMTP server
This is the ideal way to do it, as the tests can be totaly
- Wiser with WiserAssertions
- Dumbster
- GreenMail

- Drawbacks
    - Dependency of the test programming language. SDK or additional coding could be needed.
- Pros
    - Test are totally autonomous and independent this is a principle when coding tests.

Quick exmple: Using Wiser

### Approach 3 - Using embebed SMTP server and Property Testing
- This is the ideal way to do it, as the tests are staill can be totally independent and autonomous.
- Drawbacks
    - We are not use to think in the program properties, so it could be a bit painfull at the begining.
- Pros
    - This is the next level on building tests. You should focus on describing the properties that your functionality should meet instead of thinking on specific cases that usually are not the best ones


docker run -it -p 25:25 -p 80:80 --name smtp fake-smtp fake-smtp-server -s 25 -h 80

