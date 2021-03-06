## How to Mock a SMTP Service

---

## Agenda
- What is SMTP |
- How it works |
- How to Mock a SMTP - (Some Approaches) |

---

## What is SMTP

---

## How it Works

---

## How to Mock a SMTP
- Approach 1 - Using a Fake SMTP |
- Approach 2 - Using embebed SMTP server |
- Approach 3 - Add Property Testing |

---

### A1 - Using a Fake SMTP

What do you need or what should you look for?. 

- Basic - SMTP protocol :) obvoiusly. |
- Highly Desirable - Service API. |
- Desirable - SDK for your target language. | 

--- 

This is the minimum that you should have to mock a **SMTP** service and some times, it would be the only option.

---

### **fake-smtp-server nodejs** 
    - Accomplishes with the first two

### FakeSMTP - java
    - Accomplishes only the first one.

---

### Drawback
    - Test would depent on an external service
    - Either additional code would be needed to consume the API 
    or manual verification.
### Pros
    - The mayority of options are here.
    - Indepency of the testing programming language.

---

### Quick exmple: Using fake-smtp-server nodejs

```
docker run -it -p 25:25 -p 80:80 \
    --name smtp fake-smtp \
    fake-smtp-server -s 25 -h 80
```

---

### A2 - Using embebed SMTP server
This is a better way to do it, as the tests can be totally independent and autonomous.

- Wiser with WiserAssertions** |
- Dumbster |
- GreenMail |

---

### Drawbacks
    - Dependency of the test programming language. 
    SDK or additional coding could be needed.
### Pros
    - Test are totally autonomous and independent 
    this is a good practice when coding tests.

---

### Quick exmple: Using Wiser

---

### A3 - Using embebed SMTP server and Property Testing
This is the ideal way to do it, as the tests are still totally independent and autonomous but you test your application properties.

---

### Drawbacks
    - We are not use to think in the program properties, 
    so it could be a bit painfull at the begining.
    - It will slow down tests execution.
### Pros
    - This is the next level on building tests. 
    You should focus on describing the properties 
    that your functionality should meet instead of 
    thinking on specific cases that usually are not 
    the best ones

---

### Quick exmple: Using Wiser + vavr-test

---

## Q & A

---

# Thanks!

Víctor Méjia