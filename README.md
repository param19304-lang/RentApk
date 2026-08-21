# Rent & Property Management — Android App

Kotlin + Jetpack Compose + Material 3 + MVVM + Room + Hilt + DataStore + WorkManager.

## Status: Phase 1 complete

Implemented now:
- Project setup, Gradle config, package structure
- Theme system: Light/Dark/System + 8 ready-made palettes (Mono, Midnight, Ocean,
  Emerald, Royal, Sunset, Rose, Graphite) + Custom accent picker, live preview cards,
  persisted via DataStore
- Navigation: bottom bar (Dashboard / Properties / Rent / Payments / More) + More menu
- Room database: all 9 entities (Property, Unit, Tenant, Lease, Rent, Payment,
  Expense, Document, Reminder) with FKs/indexes, ready for Phase 2 features
- Property management (CRUD) and Unit management (CRUD) under each property
- Tenant management (CRUD)
- Lease management: create lease (property/unit/tenant/dates/rent/deposit/due
  day/grace/late fee/notice/escalation), auto-blocks a second active lease on an
  occupied unit, terminate lease (frees the unit, preserves history)
- Rent generation: one tap generates this month's rent record for every active
  lease, carrying forward unpaid balance as `previousOutstanding`
- Payment recording: full or partial payments, optional advance-payment support,
  auto-recalculates remaining balance and status, every payment kept (never
  overwritten/deleted)
- Dashboard: total properties/units, occupied/vacant, expected vs collected rent
  this month, pending/overdue rent, expenses this month, net income, quick actions
- Settings: appearance, app theme, landlord name, currency symbol, default due
  day/late fee
- Unit tests for the rent math (total payable, partial/advance payments, status
  transitions) and lease status calculation

Not yet built (Phase 2/3/4, entities are already in place for these):
Expenses UI, Reports, PDF receipts, Notifications/WorkManager reminders,
Backup & Restore, Documents UI, App PIN/biometric security, Room migrations
(currently `fallbackToDestructiveMigration()`), instrumented UI tests, cloud sync,
AI assistant.

## Project structure

```text
RentManagement/
├── .github/workflows/android-build.yml   # CI: builds & tests on every push
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
└── app/
    ├── build.gradle.kts
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   └── java/com/example/rentmanagement/
        │       ├── MainActivity.kt
        │       ├── RentApplication.kt
        │       ├── data/
        │       │   ├── database/      # AppDatabase, Converters
        │       │   ├── dao/           # Room DAOs (9)
        │       │   ├── entities/      # Room entities (9)
        │       │   ├── repository/    # Repository interfaces + impls
        │       │   └── preferences/   # DataStore: ThemePreferences, AppPreferences
        │       ├── domain/
        │       │   ├── model/         # Enums
        │       │   └── usecase/       # GenerateMonthlyRentUseCase, RecordPaymentUseCase,
        │       │                      # CalculateLeaseStatusUseCase, GetDashboardStatsUseCase
        │       ├── di/                # Hilt modules
        │       ├── ui/
        │       │   ├── navigation/    # Routes, NavGraph, bottom bar
        │       │   ├── theme/         # Palettes + Compose theme
        │       │   ├── components/    # Reusable composables
        │       │   ├── dashboard/ properties/ units/ tenants/ leases/ rent/ payments/ more/ settings/
        │       └── utils/             # DateUtils, CurrencyFormatter, RentCalculator, Constants
        └── test/java/com/example/rentmanagement/   # JUnit tests
```

## Gradle dependencies (see `app/build.gradle.kts` for full list)

Compose BOM 2024.06.00, Material3, Navigation Compose 2.7.7, Room 2.6.1, Hilt
2.51.1 (+ hilt-navigation-compose, hilt-work), DataStore Preferences 1.1.1,
WorkManager 2.9.0, Coroutines 1.8.1. AGP 8.5.0, Kotlin 1.9.24, compileSdk/targetSdk
34, minSdk 24.

## Database schema (v1)

- **properties**(id, name, type, address, city, state, pinCode, photoUri, ownerName,
  contactNumber, notes, createdAt, isDeleted)
- **units**(id, propertyId→properties, unitName, floor, monthlyRent,
  securityDeposit?, maintenanceCharge?, electricityCharge?, waterCharge?, status,
  currentTenantId?, notes, isDeleted)
- **tenants**(id, fullName, phoneNumber, email?, dateOfBirth?, idType?, idNumber?,
  address?, emergencyContact?, occupants, profilePhotoUri?, notes, isDeleted)
- **leases**(id, propertyId, unitId, tenantId, startDate, endDate, monthlyRent,
  securityDeposit, rentDueDay, gracePeriodDays, lateFee, noticePeriodDays,
  rentEscalationPercent, agreementDocumentUri?, status, terminatedAt?, isDeleted)
- **rent_records**(id, leaseId, tenantId, propertyId, unitId, billingMonth,
  rentAmount, maintenance, otherCharges, previousOutstanding, lateFee,
  totalPayable, amountPaid, remainingAmount, dueDate, status)
- **payments**(id, rentId, tenantId, propertyId, unitId, amount, paymentDate,
  paymentMethod, referenceNumber?, notes?, receiptNumber?, isDeleted)
- **expenses**(id, propertyId, unitId?, category, amount, date, description?,
  vendor?, invoiceImageUri?, notes?, isDeleted)
- **documents**(id, propertyId?, tenantId?, leaseId?, category, name, uri,
  uploadedAt, notes?, isDeleted)
- **reminders**(id, type, relatedId, title, message, scheduledAt, isEnabled, isSent)

All FKs use RESTRICT (or CASCADE only for property→unit, since deleting a
property should remove its units, but nothing should ever cascade-delete
tenants/leases/rent/payments — see business rules below). Deletions elsewhere
are soft (`isDeleted` flag) so historical financial records are never lost.

## Business rules implemented

1. A unit can have only one **active** lease at a time (`LeaseViewModel.createLease`
   checks `getActiveLeaseForUnit` first).
2. Rent is generated per lease (`GenerateMonthlyRentUseCase`), never per tenant
   directly.
3. Payments are always tied to a rent record and are additive/immutable — no
   payment row is ever edited or deleted by the app.
4. Partial payments supported; status becomes `PARTIALLY_PAID` (or `OVERDUE` if
   past due).
5. Advance payments are opt-in per payment (checkbox in Record Payment); when
   disabled, payment is rejected if it exceeds the remaining balance.
6. Unpaid remainder from a lease's last rent record carries forward as
   `previousOutstanding` on the next generated record.
7. Property → Unit uses cascading soft state; Tenant/Lease deletion never
   cascades into Rent/Payment history (business rules #10–#12 in the spec).
8. Formulas: `TotalPayable = rent + maintenance + otherCharges + previousOutstanding + lateFee`,
   `Remaining = TotalPayable − AmountPaid` (see `utils/RentCalculator.kt`, unit-tested).

## Build & run instructions

**Locally (Android Studio):** open the `RentManagement/` folder as a project.
Android Studio will offer to generate the Gradle wrapper (`./gradlew`) on first
sync — accept it, then Run.

**From the command line**, if you have Gradle installed:
```
cd RentManagement
gradle :app:assembleDebug
```
> Note: this repo intentionally does not commit `gradle-wrapper.jar` (it's a
> binary and this environment had no network access to fetch it). Either let
> Android Studio generate it on first open (`File > Sync`), or run
> `gradle wrapper --gradle-version 8.7` once yourself, or just use a locally
> installed `gradle` as shown above. CI does not need it either — see below.

**Via GitHub Actions (as requested):** push this repo to GitHub. The included
workflow at `.github/workflows/android-build.yml` runs on every push/PR,
installs JDK 17 + Android SDK + Gradle 8.7, runs unit tests, builds
`app-debug.apk`, and uploads it as a workflow artifact you can download from
the Actions tab.

## Testing instructions

- `app/src/test/.../RentCalculatorTest.kt` — total payable, partial payments,
  advance payments, status transitions, rejected negative/zero payments.
- `app/src/test/.../CalculateLeaseStatusUseCaseTest.kt` — ACTIVE / EXPIRING_SOON /
  EXPIRED / TERMINATED transitions.
- Run locally: `gradle testDebugUnitTest`. Runs automatically in CI.
- Instrumented/UI tests: not yet added (Phase 3).

## Future extension points (already designed for)

- **Cloud sync**: repository interfaces are the only thing ViewModels talk to —
  swap `PropertyRepositoryImpl` etc. for a sync-aware implementation without
  touching UI/domain code.
- **Migrations**: `DatabaseModule` currently uses
  `fallbackToDestructiveMigration()`; replace with real `Migration` objects
  before shipping v2 of the schema.
- **AI assistant**: should call the same `domain/usecase` and `data/repository`
  layer the UI uses — never touch DAOs/Room directly — so it inherits all
  validation and business rules for free.
- **Notifications**: `ReminderEntity` + `ReminderDao` are ready; a WorkManager
  periodic worker reading `getDueReminders()` is the natural next step.
- **Multi-user/roles**: repositories are already the seam where a `userId`
  scoping filter would be added.

## Continuing the build

This was generated in one pass covering Phase 1 of the spec. For follow-up
requests, tell me what to add next (Expenses UI, Reports, PDF receipts,
Notifications, Backup/Restore, Security, or bug fixes) and I'll modify only the
affected files rather than regenerating the project.
