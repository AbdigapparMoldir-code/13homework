```mermaid
```mermaid
%% Mermaid Use Case Diagram
%% Library Network System

usecaseDiagram

title Система управления сетью библиотек

%% === Actors ===
actor Reader as "Читатель"
actor Librarian as "Библиотекарь"
actor Admin as "Администратор"

%% === Inheritance ===
Librarian --|> Reader
Admin --|> Librarian

%% === Use Cases (Reader) ===
usecase UC_Register as "Регистрация пользователя"
usecase UC_ViewSearch as "Просмотр и поиск книг"
usecase UC_Search as "Поиск по автору/названию/жанру"
usecase UC_ViewAvailable as "Просмотр доступных книг"
usecase UC_Reserve as "Бронирование книги"
usecase UC_CancelReserve as "Отмена бронирования"
usecase UC_History as "Просмотр истории бронирований"

%% === Use Cases (Librarian) ===
usecase UC_ManageBooks as "Управление книгами (добавление/удаление)"
usecase UC_LoanReturn as "Учет выданных/возвращенных книг"
usecase UC_ActiveRes as "Просмотр списка активных бронирований"
usecase UC_Catalog as "Управление каталогом"

%% === Use Cases (Admin) ===
usecase UC_Branches as "Управление филиалами"
usecase UC_UserAccounts as "Управление учетными записями"
usecase UC_Analytics as "Просмотр аналитики"

%% === Email confirmation (extend) ===
usecase UC_EmailConfirm as "Подтверждение почты"

%% === Actor → Use Case relations ===
Reader --> UC_Register
Reader --> UC_ViewSearch
Reader --> UC_Reserve
Reader --> UC_CancelReserve
Reader --> UC_History

Librarian --> UC_ManageBooks
Librarian --> UC_LoanReturn
Librarian --> UC_ActiveRes
Librarian --> UC_Catalog

Admin --> UC_Branches
Admin --> UC_UserAccounts
Admin --> UC_Analytics

%% === Include Relations ===
UC_ViewSearch ..> UC_Search : <<include>>
UC_ViewSearch ..> UC_ViewAvailable : <<include>>

UC_Reserve ..> UC_Search : <<include>>
UC_Reserve ..> UC_ViewAvailable : <<include>>

UC_ManageBooks ..> UC_Catalog : <<include>>

%% === Extend Relation ===
UC_Register ..> UC_EmailConfirm : <<extend>>
```


