# 1.2 Problem Statement

In the early days of software engineering, we built **Monoliths**. A monolith is an application where everything—from the user interface to the database connections and business logic—is tightly packaged into a single executable process. The main advantage of a monolith was its predictability when things went wrong. If the application crashed, the algorithm to fix it was straightforward: you restarted the monolith, and everything generally worked again. However, the fatal flaw of the monolith was its fragility. If a minor, non-critical feature (like generating a PDF receipt) encountered an memory leak, it could bring down the entire system, preventing users from logging in or making purchases. 

Today, the industry has shifted to **Microservices**. In a modern enterprise, an application might consist of hundreds or even thousands of independent microservices. This architecture solves the monolithic fragility problem: if the PDF-generating service crashes, the rest of the application remains online. For example, a user might not be able to view their account balance, but they can still successfully complete a transaction.

However, microservices introduce a entirely new set of problems. In a distributed system with hundreds of moving parts, it is incredibly difficult to know exactly what is broken at any given moment. Furthermore, there is an unwritten rule in distributed systems: **no matter how small the probability of a component failing, if it exists, it will eventually fail.** It is practically impossible to achieve 100% uptime. 

The core problem, therefore, is not how to write perfect code that never crashes. The problem is how to design the communication between these thousands of microservices so that a local failure does not cascade into a global outage. 

If we attempt to bolt on fault tolerance to an existing system after it is already built, it is an incredibly complex and painful process. The true challenge lies in building a system that is **fault-tolerant by design**. This requires adopting a "reverse-engineering" mindset: instead of asking "How do we keep this microservice from turning off?", we must look at the system architecture, mentally turn off a microservice, and ask, "How does the system continue to provide value to the user right now?"

This thesis addresses the problem of designing a robust, distributed notification system where failures are expected, contained, and gracefully handled without degrading the core user experience.
