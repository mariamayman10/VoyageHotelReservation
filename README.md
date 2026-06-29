# Voyage

A hotel reservation REST API built with Spring Boot and PostgreSQL.

---

## Tech Stack

- **Java 17** + **Spring Boot 3**
- **Spring Security** + **JWT** for authentication
- **Spring Data JPA** + **Hibernate** for ORM
- **PostgreSQL** for persistence
- **Maven** for dependency management

---

## Features

### Auth
- Register and login with JWT-based authentication
- Role-based access control: `CUSTOMER`, `MANAGER`, `ADMIN`

### Hotels
- Public browsing and search
- Managers create and manage their own hotels
- Amenity management via a shared reference list

### Rooms
- Nested under hotels, publicly searchable by availability and criteria
- Date-based availability check against existing bookings
- Managers manage their own hotel rooms with status filtering
- Room statuses: `AVAILABLE`, `MAINTENANCE`

### Bookings
- Customers create, view, and cancel bookings
- Date overlap validation to prevent double booking
- Soft cancel — booking record is preserved with `CANCELLED` status
- Managers view bookings for their own hotels with filters
- Admins view all bookings system-wide with aggregate stats

### Payments
- Payment created on booking, processed via a dedicated pay endpoint
- Refund triggered on booking cancellation if payment was completed
- Payment statuses: `PENDING`, `COMPLETED`, `CANCELLED`, `REFUNDED`
- Email notification sent to customer on booking confirmation and cancellation

---

## Entity Overview

| Entity | Key Fields |
|---|---|
| `User` | name, email, passwordHash, phoneNumber, isDisabled, role |
| `Hotel` | name, description, city, contactPhone, contactEmail, managerId |
| `Amenity` | name, icon |
| `Room` | number, floor, capacity, pricePerNight, type, status, hotelId |
| `Booking` | userId, hotelId, roomId, checkInDate, checkOutDate, totalPrice, status |
| `Payment` | bookingId, amount, currency, status, paidAt |

---

## API Endpoints

### Auth
| Method | Endpoint | Access |
|---|---|---|
| `POST` | `/api/auth/register` | Public |
| `POST` | `/api/auth/login` | Public |

### Hotels
| Method | Endpoint | Access |
|---|---|---|
| `GET` | `/api/hotels` | Public |
| `GET` | `/api/hotels/{id}` | Public |
| `POST` | `/api/hotels` | Manager |
| `PUT` | `/api/hotels/{id}` | Manager |
| `DELETE` | `/api/hotels/{id}` | Manager |

### Amenities
| Method | Endpoint | Access |
|---|---|---|
| `GET` | `/api/amenities` | Public |
| `POST` | `/api/amenities` | Admin |
| `PUT` | `/api/amenities/{id}` | Admin |
| `DELETE` | `/api/amenities/{id}` | Admin |

### Rooms
| Method | Endpoint | Access |
|---|---|---|
| `GET` | `/api/rooms/{id}` | Public |
| `GET` | `/api/rooms/available` | Public |
| `GET` | `/api/rooms/my` | Manager |
| `POST` | `/api/rooms` | Manager |
| `PUT` | `/api/rooms/{id}` | Manager |
| `DELETE` | `/api/rooms/{id}` | Manager |

### Bookings
| Method | Endpoint | Access |
|---|---|---|
| `POST` | `/api/bookings` | Customer |
| `GET` | `/api/bookings/my` | Customer |
| `GET` | `/api/bookings/my/{id}` | Customer |
| `DELETE` | `/api/bookings/{id}` | Customer |
| `GET` | `/api/hotels/{hotelId}/bookings` | Manager |
| `GET` | `/api/hotels/{hotelId}/bookings/{id}` | Manager |

### Payments
| Method | Endpoint | Access |
|---|---|---|
| `POST` | `/api/payments/pay/{bookingId}` | Customer |
| `POST` | `/api/payments/refund/{bookingId}` | Customer |

## Project Structure

```
src/main/java/org/example/voyage/
│
├── auth/               # Registration, login, JWT filter
├── user/               # User entity, controller, service, repository
├── hotel/              # Hotel entity, controller, service, repository
├── amenity/            # Amenity entity, controller, service, repository
├── room/               # Room entity, controller, service, repository, specifications
├── booking/            # Booking entity, controller, service, repository, specifications
├── payment/            # Payment entity, controller, service, repository
├── notification/       # Email service, notification publisher
├── security/           # JwtFilter, UserDetailsImpl, UserPrincipal
├── exception/          # Global Exception Handler and Custom created exception
└── config/             # Configurations for Jwt, OpenApi, Security, RabbitMQ
```
