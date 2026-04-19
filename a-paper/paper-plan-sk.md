# Plán kapitol bakalárskej práce: Robustný systém pre rozposielanie upozornení

1. Úvod
    * 1.1 Význam robustnosti v ére distribuovaných systémov:
        * Paradigma "všetko sa môže pokaziť": Prechod od monolitickej stability k distribuovanej komplexnosti.
        * Očakávania od moderného softvéru: Korektnosť, výkon a bezpečnosť pri nepredvídateľnom správaní okolia.
    * 1.2 Tri piliere kvality dátových systémov (podľa M. Kleppmanna):
        * Spoľahlivosť (Reliability): Zachovanie funkčnosti pri hardvérových a softvérových chybách.
        * Škálovateľnosť (Scalability): Stratégie spracovania rastúcej záťaže a objemu dát.
        * Udržateľnosť (Maintainability): Návrh systému s ohľadom na evolúciu a operatívu.
    * 1.3 Teoretický rámec odolnosti voči poruchám:
        * Rozdiel medzi chybou (Fault) a zlyhaním (Failure): Mechanizmy zabraňujúce šíreniu lokálnych porúch.
        * Klasifikácia porúch: Hardvérové defekty, softvérové anomálie a vplyv ľudského faktora.
    * 1.4 Inžinierstvo zamerané na odolnosť (Resilience Engineering):
        * Prečo je 100 % uptime nedosiahnuteľný: Teoretické a praktické limity dostupnosti.
        * Chaos Engineering: Metodológia úmyselného vstrekovania chýb pre validáciu robustnosti (napr. princípy Chaos Monkey).
    * 1.5 Ciele práce a hlavná téza: Implementácia systému schopného "postupnej degradácie" (Graceful Degradation) pri kritických výpadkoch komponentov.

2. Teoretické východiská a analýza
    * 2.1 Monolit vs. Mikroslužby:
        * Výhody monolitu: Jednoduchosť nasadenia, absencia sieťových oneskorení, transakčná celistvosť (ACID).
        * Výhody mikroslužieb: Izolácia porúch (Fault Isolation), nezávislé škálovanie, technologická flexibilita.
        * Záver: Prečo sú pre systémy upozornení dnes výhodnejšie mikroslužby (ochrana pred domino efektom).
    * 2.2 Odolnosť voči poruchám a degradácia systému:
        * Definícia Fault Tolerance vs. High Availability.
        * Koncept System Degradation: Ako systém pokračuje v čiastočnej prevádzke, ak je jeden komponent (napr. Weather API) nedostupný.
    * 2.3 Asynchrónna komunikácia a EDA:
        * Prečo synchrónne požiadavky (REST) robia systém krehkým.
        * EDA (Event-Driven Architecture) ako spôsob prerušenia tesných väzieb medzi službami.
    * 2.4 Porovnanie sprostredkovateľov správ: SQS vs. Kafka:
        * Kafka: Vysoká priepustnosť, uchovávanie histórie, ale zložitosť nastavenia, správy klastra a ZooKeeper/KRaft.
        * SQS: Plne spravovaná (Managed) služba, nulové náklady na údržbu, jednoduchosť konfigurácie „out-of-the-box“.
        * Odôvodnenie výberu: Voľba SQS na základe kritéria minimalizácie operačných rizík a jednoduchosti nastavenia pre projekt.

3. Funkčná špecifikácia aplikácie
    * 3.1 Používateľské roly: Registrácia, správa prahových hodnôt (pravidiel).
    * 3.2 Hlavné scenáre:
        * Zber údajov o počasí (každých 15 minút).
        * Kontrola podmienok podľa prediktívneho modelu (N hodín pred udalosťou).
        * Garantované odoslanie upozornenia pri aktivácii triggera.
    * 3.3 Požiadavky na odolnosť: Systém musí zostať funkčný aj pri výpadku ktorejkoľvek služby (okrem databázy cieľovej služby).

4. Architektonický návrh – zameranie na rezilienciu (odolnosť)
    * 4.1 Topológia systému: Schéma interakcie cez SNS/SQS (Fan-out).
    * 4.2 Vzory odolnosti (Resilience patterns):
        * Circuit Breaker (Resilience4j): Ochrana Weather Service pred zamrznutím pri výpadkoch externého API.
        * Dead Letter Queue (DLQ): Kam odchádzajú správy, ktoré sa nepodarilo spracovať, a ako zachraňujú systém pred nekonečnými cyklami chýb.
        * Idempotencia: Ako neposielať používateľovi 100 rovnakých e-mailov, ak sa Decision Service reštartuje.
    * 4.3 Model degradácie: Tabuľka stavov (Čo funguje, ak vypadne služba X).

5. Realizácia a implementácia
    * 5.1 Technologický stack: Java 21 (Virtual Threads ako faktor výkonu), Spring Boot 3.5, Spring Cloud AWS.
    * 5.2 Konfigurácia infraštruktúry: Použitie LocalStack na simuláciu SQS/SNS v Docker kontajneroch.
    * 5.3 Implementácia asynchrónnych listenerov: Nastavenie Long Polling pre optimalizáciu zdrojov.

6. Overenie a testovanie degradácie
    * 6.1 Simulácia výpadkov (Chaos Engineering light): Čo sa stane, ak „zastavíme“ kontajner Weather Service? (Decision Service musí pokračovať v práci so starými údajmi).
    * 6.2 Testovanie frontov (queues): Kontrola hromadenia správ v SQS pri vypnutej službe Notification Service a ich následné spracovanie po obnovení.
    * 6.3 Metriky: Čas reakcie systému na udalosť v podmienkach záťaže.

7. Záver
    * Potvrdenie, že EDA a SQS umožnili vytvoriť systém odolný voči kaskádovým poruchám.
    * Budúci rozvoj: Pridanie nových kanálov (Telegram, SMS), podpora zložitých pravidiel (Machine Learning pre predpovede).

---
**Poznámky k implementácii:**
* Hlavným cieľom je vytvoriť architektúru odolnú voči poruchám. Detaily systému notifikácií sú len príkladom, na ktorom sa táto odolnosť demonštruje.
* Práca obsahuje jasnú špecifikáciu funkčnosti aplikácie.
* V teoretickej časti je porovnaná mikroservisná a monolitická architektúra s dôrazom na výhody mikroslužieb v súčasnom vývoji.
* Je vysvetlené, ako asynchrónna komunikácia a Event-Driven Architecture zvyšujú odolnosť systému.
* Amazon SQS je zvolený pre jeho jednoduchšiu konfiguráciu a správu v porovnaní s platformou Kafka.
* Práca podrobne vysvetľuje pojmy "postupná degradácia" (system degradation) and "odolnosť voči poruchám" (fault tolerance).
