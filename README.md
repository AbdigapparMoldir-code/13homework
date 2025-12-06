```mermaid
stateDiagram-v2
    [*] --> Idle

    state Idle {
    }

    state WaitingForMoney {
    }

    state MoneyReceived {
    }

    state TicketDispensed {
    }

    state TransactionCanceled {
    }

    Idle --> WaitingForMoney : Выбор билета / selectTicket()
    WaitingForMoney --> MoneyReceived : Внесение достаточной суммы / insertMoney(amount)
    MoneyReceived --> TicketDispensed : Выдать билет / dispense()

    WaitingForMoney --> TransactionCanceled : Отмена / cancel()
    MoneyReceived --> TransactionCanceled : Отмена до выдачи / cancel()

    TicketDispensed --> Idle : Завершение / complete()
    TransactionCanceled --> Idle : Сброс / reset()

    %% Optional transition
    MoneyReceived --> WaitingForMoney : Недостаточно денег / awaitMoreMoney()
```
