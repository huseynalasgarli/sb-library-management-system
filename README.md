# 📚 Library Management System

A full-featured backend REST API for managing a library — built with Spring Boot, PostgreSQL, JWT authentication, and Stripe payments.

---

## 🧰 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.0.4 |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Payments | Stripe Java SDK 27.1.0 |
| Email | Spring Mail (Gmail SMTP) |
| Utilities | Lombok, Bean Validation |
| Build Tool | Maven |

---

## 🏗️ Architecture Overview

```
React Frontend (port 5173)
        ↕ REST API (port 5000)
Spring Boot Application
        ↕ JPA / Hibernate
    PostgreSQL Database
        +
    Gmail SMTP  (email notifications)
        +
    Stripe API  (payment processing)
```

### Package Structure

```
com.hsyn
├── configurations/     # JWT provider, validator, security config
├── controller/         # REST controllers (HTTP layer)
├── domain/             # Enums (roles, statuses, types)
├── event/              # Spring event publisher & listener (payment events)
├── exception/          # Custom exceptions & global handler
├── mapper/             # Entity ↔ DTO converters
├── model/              # JPA entities (DB tables)
├── payload/
│   ├── dto/            # Data Transfer Objects
│   ├── request/        # Incoming request bodies
│   └── response/       # Outgoing response shapes
├── repository/         # Spring Data JPA repositories
└── service/            # Business logic (interfaces + implementations)
    ├── gateway/        # Stripe integration
    └── impl/           # Service implementations
```

---

## ✨ Features

### 🔐 Authentication & Security
- JWT-based stateless authentication (24-hour token expiry)
- BCrypt password hashing
- Role-based access control: `ROLE_USER` and `ROLE_ADMIN`
- Google OAuth2 support (data model ready)
- Forgot password / reset password via email token (5-minute expiry)

### 📖 Book Management
- Full CRUD for books (admin only for create)
- Soft delete (deactivate) and hard delete
- Bulk book import
- Advanced search with filters: title, author, ISBN, genre, availability
- Paginated and sortable results
- Genre system with parent/child hierarchy (e.g. Fiction → Sci-Fi → Cyberpunk)

### 🔄 Book Loans
- Checkout with subscription-based limits (max books, max days)
- Check-in with condition reporting (returned, lost, damaged)
- Renewal support (up to 2 renewals per loan, blocked if overdue)
- Overdue detection and batch status update
- Blocks checkout if user has overdue books

### 📅 Reservations
- Queue-based reservation system for unavailable books
- Position tracking in queue
- Auto-expiry when pickup window passes
- Admin fulfillment (triggers checkout automatically)
- Cancel own reservation (admins can cancel any)

### 💸 Fines
- Admin-created fines linked to specific book loans
- Fine types: overdue, damage, loss, processing
- Payment via Stripe
- Admin waiver with reason tracking
- Full audit trail (processed by, waived by, timestamps)

### 💳 Payments (Stripe)
- Stripe Checkout Session integration
- Supports: membership subscriptions and fine payments
- Payment metadata stored for post-payment reconciliation
- Event-driven post-payment actions (subscription activation)
- Payment status lifecycle: PENDING → PROCESSING → SUCCESS / FAILED

### 🎫 Subscriptions
- Users subscribe to plans (e.g. Basic, Premium)
- Subscription stays inactive until payment confirmed
- Plan details snapshotted at subscription time (price changes don't affect existing subscribers)
- Auto-deactivation of expired subscriptions
- Cancellation with reason

### ⭐ Reviews & Wishlist
- Users can review books (1–5 star rating + text)
- One review per user per book
- Personal wishlist with notes

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 14+
- A Stripe account (for payments)
- A Gmail account with App Password (for emails)

### Environment Variables

Create an `application-local.properties` file (already git-ignored) with:

```properties
DB_URL=jdbc:postgresql://localhost:5432/library_db
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password

MAIL_USERNAME=your_gmail@gmail.com
MAIL_PASSWORD=your_gmail_app_password

STRIPE_SECRET_KEY=sk_test_...
STRIPE_PUBLIC_KEY=pk_test_...
STRIPE_CALLBACK_URL=http://localhost:5173
```

### Running the Application

```bash
# Clone the repository
git clone https://github.com/your-username/library-management-system.git
cd library-management-system

# Build
./mvnw clean install

# Run
./mvnw spring-boot:run
```

The API starts on **http://localhost:5000**

### Default Admin Account

On first startup, an admin account is created automatically:
- **Email:** `huseynalasgarli@gmail.com`
- **Password:** `huseynalasgarli`

> ⚠️ Change these credentials immediately in `DataInitializationComponent.java` before deploying.

---

## 📡 API Reference

All endpoints require `Authorization: Bearer <jwt>` unless noted.

### Auth — `/auth/**` (public)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/signup` | Register a new user |
| POST | `/auth/login` | Login, receive JWT |
| POST | `/auth/forgot-password` | Send password reset email |
| POST | `/auth/reset-password` | Reset password with token |

### Books — `/api/books/**`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/admin/books` | ADMIN | Create a single book |
| POST | `/api/books/bulk` | Any | Bulk create books |
| GET | `/api/books/{id}` | Any | Get book by ID |
| GET | `/api/books?searchTerm=&genreId=&availableOnly=` | Any | Search books |
| POST | `/api/books/search` | Any | Advanced search (request body) |
| PUT | `/api/books/{id}` | Any | Update book |
| DELETE | `/api/books/{id}` | Any | Soft delete book |
| DELETE | `/api/books/{id}/permanent` | Any | Hard delete book |
| GET | `/api/books/stats` | Any | Total active / available counts |

### Book Loans — `/api/book-loans/**`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/book-loans/checkout` | Checkout a book (current user) |
| POST | `/api/book-loans/checkout/user/{userId}` | Checkout for specific user (admin) |
| POST | `/api/book-loans/checkin` | Return a book |
| POST | `/api/book-loans/renew` | Renew a loan |
| GET | `/api/book-loans/my` | Get my loans (filterable by status) |
| POST | `/api/book-loans/search` | Search all loans (admin) |
| POST | `/api/book-loans/admin/update-overdue` | Batch mark overdue loans |

### Reservations — `/api/reservations/**`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/reservations` | Create reservation (current user) |
| POST | `/api/reservations/user/{userId}` | Create for specific user (admin) |
| DELETE | `/api/reservations/{id}` | Cancel reservation |
| POST | `/api/reservations/{id}/fulfill` | Fulfill reservation (admin) |
| GET | `/api/reservations/my` | Get my reservations |
| GET | `/api/reservations` | Search all reservations (admin) |

### Fines — `/api/fines/**`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/fines` | Create a fine (admin) |
| POST | `/api/fines/{id}/pay` | Initiate fine payment via Stripe |
| POST | `/api/fines/waive` | Waive a fine (admin) |
| GET | `/api/fines/my` | Get my fines |
| GET | `/api/fines` | Get all fines with filters (admin) |

### Payments — `/api/payments/**`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/payments/verify` | Verify Stripe payment after redirect |
| GET | `/api/payments` | List all payments (admin, paginated) |

### Subscriptions — `/api/subscriptions/**`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/subscriptions/subscribe` | Subscribe to a plan (initiates Stripe payment) |
| GET | `/api/subscriptions/user/active` | Get current user's active subscription |
| GET | `/api/subscriptions/admin` | List all subscriptions (admin) |
| POST | `/api/subscriptions/cancel/{id}` | Cancel a subscription |
| POST | `/api/subscriptions/activate` | Activate subscription after payment |
| GET | `/api/subscriptions/admin/deactivate` | Deactivate expired subscriptions |

### Subscription Plans — `/api/subscription-plans/**`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/subscription-plans` | Any | List all active plans |
| POST | `/api/subscription-plans/admin/create` | ADMIN | Create a plan |
| PUT | `/api/subscription-plans/admin/{id}` | ADMIN | Update a plan |
| DELETE | `/api/subscription-plans/admin/{id}` | ADMIN | Delete a plan |

---

## 💳 Payment Flow

```
1. User calls POST /api/subscriptions/subscribe  (or /api/fines/{id}/pay)
          ↓
2. Backend creates a Payment record (status: PENDING)
          ↓
3. Backend creates a Stripe Checkout Session
          ↓
4. Backend returns { checkoutUrl } to frontend
          ↓
5. Frontend redirects user to Stripe's hosted payment page
          ↓
6. User completes payment on Stripe
          ↓
7. Stripe redirects to: /payment-success/{paymentId}?session_id=...
          ↓
8. Frontend calls POST /api/payments/verify  { stripeSessionId }
          ↓
9. Backend verifies with Stripe, marks Payment as SUCCESS
          ↓
10. Spring event fires → Subscription activated (for membership payments)
```

---

## 🗄️ Data Model

```
User
 ├── BookLoan → Book → Genre
 │       └── Fine → Payment
 ├── Reservation → Book
 ├── Subscription → SubscriptionPlan
 ├── BookReview → Book
 ├── Wishlist → Book
 └── PasswordResetToken
```

### Key Business Rules
- Users need an **active subscription** to check out books
- Users with **overdue books** cannot check out new ones
- Books can be renewed up to **2 times**, only if not overdue
- Reservations are only allowed for **unavailable** books
- A user can have at most **5 active reservations**
- Subscriptions stay **inactive** until payment is confirmed

---

## ⚙️ Database

- `spring.jpa.hibernate.ddl-auto=update` — Hibernate auto-creates/alters tables on startup
- No manual SQL migrations needed for development
- `spring.jpa.show-sql=true` — SQL queries logged to console

---

## 🔒 Security

- All `/api/**` endpoints require authentication
- `/api/admin/**` and `/api/subscription-plans/admin/**` require `ROLE_ADMIN`
- CORS configured for `http://localhost:5173` (dev) and `https://hsynlibrary.com` (prod)
- JWT tokens expire after **24 hours**
- Password reset tokens expire after **5 minutes**

---

## 📁 Configuration Files

| File | Purpose |
|---|---|
| `application.properties` | Base config, references environment variables |
---

## 👤 Author

**huseynalasgarli** — [GitHub](https://github.com/huseynalasgarli)
