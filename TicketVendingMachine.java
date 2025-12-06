public class TicketVendingMachine {
    public interface State {
        void selectTicket(String ticketType);
        void insertMoney(double amount);
        void cancel();
        void dispense();
    }
    private State idleState;
    private State waitingForMoneyState;
    private State moneyReceivedState;
    private State ticketDispensedState;
    private State transactionCanceledState;
    private State currentState;
    private double balance = 0.0;
    private double ticketPrice = 1.50; // default price
    private String selectedTicketType = null;
    private int ticketCount = 10; // total tickets available
    private double changeToReturn = 0.0;

    public TicketVendingMachine() {
        idleState = new IdleState(this);
        waitingForMoneyState = new WaitingForMoneyState(this);
        moneyReceivedState = new MoneyReceivedState(this);
        ticketDispensedState = new TicketDispensedState(this);
        transactionCanceledState = new TransactionCanceledState(this);

        currentState = idleState;
    }
    void setState(State s) { this.currentState = s; }
    State getIdleState() { return idleState; }
    State getWaitingForMoneyState() { return waitingForMoneyState; }
    State getMoneyReceivedState() { return moneyReceivedState; }
    State getTicketDispensedState() { return ticketDispensedState; }
    State getTransactionCanceledState() { return transactionCanceledState; }
    double getBalance() { return balance; }
    void addBalance(double amount) { this.balance += amount; }
    void resetBalance() { this.balance = 0.0; }
    double getTicketPrice() { return ticketPrice; }
    void setTicketPrice(double p) { this.ticketPrice = p; }
    String getSelectedTicketType() { return selectedTicketType; }
    void setSelectedTicketType(String t) { this.selectedTicketType = t; }
    int getTicketCount() { return ticketCount; }
    void setTicketCount(int c) { this.ticketCount = c; }
    void decrementTicketCount() { if (ticketCount > 0) ticketCount--; }
    void setChangeToReturn(double c) { this.changeToReturn = c; }
    double getChangeToReturn() { return changeToReturn; }
    public void selectTicket(String ticketType) { currentState.selectTicket(ticketType); }
    public void insertMoney(double amount) { currentState.insertMoney(amount); }
    public void cancel() { currentState.cancel(); }
    public void dispense() { currentState.dispense(); }
    class IdleState implements State {
        private TicketVendingMachine machine;
        IdleState(TicketVendingMachine m) { this.machine = m; }

        @Override
        public void selectTicket(String ticketType) {
            if (machine.getTicketCount() <= 0) {
                System.out.println("[Idle] Нет билетов в автомате.");
                return;
            }
            machine.setSelectedTicketType(ticketType);
            if ("VIP".equalsIgnoreCase(ticketType)) {
                machine.setTicketPrice(3.00);
            } else if ("Child".equalsIgnoreCase(ticketType)) {
                machine.setTicketPrice(0.75);
            } else {
                machine.setTicketPrice(1.50);
            }
            System.out.println("[Idle] Выбран билет: " + ticketType + ". Цена: " + machine.getTicketPrice());
            machine.setState(machine.getWaitingForMoneyState());
        }

        @Override
        public void insertMoney(double amount) {
            System.out.println("[Idle] Сначала выберите билет.");
        }

        @Override
        public void cancel() {
            System.out.println("[Idle] Нет транзакции для отмены.");
        }

        @Override
        public void dispense() {
            System.out.println("[Idle] Нечего выдавать.");
        }
    }

    class WaitingForMoneyState implements State {
        private TicketVendingMachine machine;
        WaitingForMoneyState(TicketVendingMachine m) { this.machine = m; }

        @Override
        public void selectTicket(String ticketType) {
            System.out.println("[WaitingForMoney] Уже выбран билет: " + machine.getSelectedTicketType());
        }

        @Override
        public void insertMoney(double amount) {
            if (machine.getTicketCount() <= 0) {
                System.out.println("[WaitingForMoney] Нет билетов. Возврат денег.");
                machine.setChangeToReturn(amount);
                machine.setState(machine.getTransactionCanceledState());
                machine.getTransactionCanceledState().dispense(); // handle return immediately
                return;
            }
            machine.addBalance(amount);
            System.out.printf("[WaitingForMoney] Внесено: %.2f. Баланс: %.2f (цена: %.2f)%n",
                    amount, machine.getBalance(), machine.getTicketPrice());
            if (machine.getBalance() >= machine.getTicketPrice()) {
                machine.setChangeToReturn(machine.getBalance() - machine.getTicketPrice());
                machine.setState(machine.getMoneyReceivedState());
                System.out.println("[WaitingForMoney] Достаточно средств. Переход в MoneyReceived.");
            } else {
                System.out.println("[WaitingForMoney] Недостаточно средств. Внесите ещё.");
            }
        }

        @Override
        public void cancel() {
            System.out.println("[WaitingForMoney] Транзакция отменена. Возврат денег: " + machine.getBalance());
            machine.setChangeToReturn(machine.getBalance());
            machine.resetBalance();
            machine.setState(machine.getTransactionCanceledState());
            machine.getTransactionCanceledState().dispense();
        }

        @Override
        public void dispense() {
            System.out.println("[WaitingForMoney] Недостаточно средств для выдачи.");
        }
    }

    class MoneyReceivedState implements State {
        private TicketVendingMachine machine;
        MoneyReceivedState(TicketVendingMachine m) { this.machine = m; }

        @Override
        public void selectTicket(String ticketType) {
            System.out.println("[MoneyReceived] Нельзя менять билет после внесения денег (отмените сначала).");
        }

        @Override
        public void insertMoney(double amount) {
            machine.addBalance(amount);
            machine.setChangeToReturn(machine.getBalance() - machine.getTicketPrice());
            System.out.printf("[MoneyReceived] Добавлено: %.2f. Новый баланс: %.2f. Сдача (ориентир): %.2f%n",
                    amount, machine.getBalance(), machine.getChangeToReturn());
        }

        @Override
        public void cancel() {
            System.out.println("[MoneyReceived] Отмена транзакции. Возврат: " + machine.getBalance());
            machine.setChangeToReturn(machine.getBalance());
            machine.resetBalance();
            machine.setState(machine.getTransactionCanceledState());
            machine.getTransactionCanceledState().dispense();
        }

        @Override
        public void dispense() {
            if (machine.getTicketCount() <= 0) {
                System.out.println("[MoneyReceived] Билеты закончились. Возврат денег.");
                machine.setChangeToReturn(machine.getBalance());
                machine.resetBalance();
                machine.setState(machine.getTransactionCanceledState());
                machine.getTransactionCanceledState().dispense();
                return;
            }
            machine.decrementTicketCount();
            System.out.println("[MoneyReceived] Билет выдан: " + machine.getSelectedTicketType());
            System.out.printf("[MoneyReceived] Сдача: %.2f%n", machine.getChangeToReturn());
            machine.resetBalance();
            machine.setState(machine.getTicketDispensedState());
            machine.getTicketDispensedState().dispense();
        }
    }

    class TicketDispensedState implements State {
        private TicketVendingMachine machine;
        TicketDispensedState(TicketVendingMachine m) { this.machine = m; }
        @Override
        public void selectTicket(String ticketType) {
            System.out.println("[TicketDispensed] Транзакция завершена. Сначала вернитесь в Idle.");
        }

        @Override
        public void insertMoney(double amount) {
            System.out.println("[TicketDispensed] Транзакция завершена.");
        }

        @Override
        public void cancel() {
            System.out.println("[TicketDispensed] Нельзя отменить — билет уже выдан.");
        }

        @Override
        public void dispense() {
            double change = machine.getChangeToReturn();
            if (change > 0.0) {
                System.out.printf("[TicketDispensed] Выдана сдача: %.2f%n", change);
            }
            System.out.println("[TicketDispensed] Завершение транзакции. Возврат в Idle.");
            machine.setChangeToReturn(0.0);
            machine.setSelectedTicketType(null);
            machine.setState(machine.getIdleState());
        }
    }

    class TransactionCanceledState implements State {
        private TicketVendingMachine machine;
        TransactionCanceledState(TicketVendingMachine m) { this.machine = m; }

        @Override
        public void selectTicket(String ticketType) {
            System.out.println("[Canceled] Транзакция отменена — вернитесь в Idle и выберите билет заново.");
        }

        @Override
        public void insertMoney(double amount) {
            System.out.println("[Canceled] Транзакция отменена.");
        }

        @Override
        public void cancel() {
            System.out.println("[Canceled] Уже в состоянии отмены.");
        }

        @Override
        public void dispense() {
            // return change if any
            double change = machine.getChangeToReturn();
            if (change > 0.0) {
                System.out.printf("[Canceled] Возвращаем деньги: %.2f%n", change);
            } else {
                System.out.println("[Canceled] Нечего возвращать.");
            }
            machine.setChangeToReturn(0.0);
            machine.setSelectedTicketType(null);
            machine.resetBalance();
            machine.setState(machine.getIdleState());
        }
    }
    public static void main(String[] args) {
        TicketVendingMachine vm = new TicketVendingMachine();

        System.out.println("Сценарий 1: Успешная покупка");
        vm.selectTicket("Standard");
        vm.insertMoney(1.00);
        vm.insertMoney(0.50);
        vm.dispense();
        System.out.println("\n Сценарий 2: Отмена до внесения денег");
        vm.selectTicket("Child");
        vm.cancel();
        System.out.println("\nСценарий 3: Отмена после внесения денег");
        vm.selectTicket("VIP");
        vm.insertMoney(2.00);
        vm.cancel();
        System.out.println("\n Сценарий 4: Билеты закончились ");
        vm.setTicketCount(1);
        vm.selectTicket("Standard");
        vm.insertMoney(1.50);
        vm.dispense();
        vm.selectTicket("Standard");
        vm.insertMoney(1.50);
        System.out.println("\n Конец демонстрации ");
    }
}
