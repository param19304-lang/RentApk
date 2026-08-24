# Rent & Property Management — Android App

Kotlin + Jetpack Compose + Material 3 + MVVM + Room + Hilt + DataStore + WorkManager.

## Status: Phase 1 + Phase 2 complete, navigation drawer, and UI/UX redesign in progress

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

**New this pass:**
- Multi-user auth: PBKDF2-hashed local login (no cloud), session persisted via
  DataStore. First launch forces creating the first ADMIN account
  (`AdminSetupScreen`); every account after that is created by an admin from
  Settings > Manage Users. Two roles: `ADMIN` and `USER`. Admin-only areas
  (Manage Users) are hidden from `USER` accounts in both the More menu and
  Settings. Logout button in Settings.
- Expenses module: full CRUD (category, amount, date, description, vendor,
  notes per property), wired into Dashboard's "Add Expense" quick action and
  the More menu — dashboard's Total Expenses/Net Income stats now reflect
  real data instead of always reading 0.
- App icon: adaptive icon (API 26+) + legacy mipmap fallback (API 24-25) for
  all densities, matching the Mono theme.

**Also added this pass:**
- PDF receipts: after recording a payment, "Share Receipt (PDF)" generates a
  one-page receipt (`utils/PdfReceiptGenerator.kt`, drawn directly with
  `android.graphics.pdf.PdfDocument` — no third-party library) and opens the
  system share sheet via a `FileProvider`.
- Reports: Rent Collection / Expenses / Income / Tenant tabs with a month
  stepper, each exportable as CSV or PDF (`utils/ReportExporter.kt`, generic
  CSV writer + paginated PDF table renderer), shared the same way as receipts.
- Notifications: a daily `HiltWorker` (`RentReminderWorker`) marks overdue
  rent, then posts summary notifications for rent due soon, rent overdue, and
  leases expiring soon — respecting the notifications-enabled and
  reminder-days-before settings already in Settings. Runtime POST_NOTIFICATIONS
  permission is requested on first login (API 33+).
- Backup & Restore: exports/imports the raw Room SQLite file via the system
  file picker (Storage Access Framework) — no JSON round-trip, so it's exact.
  Restore asks for confirmation, then restarts the app to load the new file.
- Documents: a flat document vault (pick a PDF/image via the system file
  picker, persist read access, tag with a category) — global for now rather
  than tenant/lease-scoped, since the picker + persisted-permission pattern is
  the part worth getting right first.

Not yet built (entities/repos already in place for most):
App PIN/biometric lock (session auth exists; device-level PIN/biometric on
top of it is still open), Room migrations (currently
`fallbackToDestructiveMigration()` — increasingly important since the schema
is now at v2), instrumented UI tests, cloud sync, AI assistant, per-tenant/
lease document scoping in the UI (schema already supports it).

## Rent Start Date (new field)

There was no field separating "when the lease begins" from "when rent
charging begins" — `GenerateMonthlyRentUseCase` only ever generated rent for
whatever billing month was currently selected, with no concept of a lease's
own start date at all (so it also wouldn't stop you from generating rent for
a month before a lease existed).

Added `LeaseEntity.rentStartDate` (defaults to `startDate`, so existing
leases are unaffected — Room schema bumped 2 → 3, `fallbackToDestructiveMigration()`
still applies). In **Add Lease**, a new "Rent start date" field appears right
after Lease end date, pre-filled to match the lease start date but editable
— e.g. for a free first month, set it a month later than the lease start.
Validated against the lease's own start/end dates in `LeaseViewModel`.

`GenerateMonthlyRentUseCase` now skips creating a rent record for any
billing month that falls before a lease's `rentStartDate`. This does **not**
prorate the first chargeable month — once generation begins, the full
`monthlyRent` applies, consistent with the rest of the app's no-proration
design. Rent start date is surfaced on the Leases list (when it differs from
lease start) and on Tenant Details' Occupancy card.

## Bug fixes this pass

- **Keyboard vs. forms**: every form screen (Property, Unit, Tenant, Lease,
  Expense, Record Payment, Settings, Login, Admin Setup, and the Add
  User/Add Document dialogs) is now wrapped in a scrollable container so a
  field hidden behind the keyboard can be scrolled into view, plus
  `android:windowSoftInputMode="adjustResize"` on `MainActivity` so the
  window actually resizes for the keyboard rather than just covering
  content. Added a reusable `Modifier.dismissKeyboardOnTap()`
  (`ui/components/KeyboardUtils.kt`) — tapping anywhere outside a field now
  clears focus and hides the keyboard, applied to every full-screen form.
- **Drawer scrolling**: `AppDrawerContent`'s `ModalDrawerSheet` is now
  wrapped in `verticalScroll` — with 11+ destinations plus the user header
  and logout, the menu no longer clips on smaller screens.
- **Tenants count**: added `TenantDao.getTenantCount()` →
  `TenantRepository.getTenantCount()` → wired into `GetDashboardStatsUseCase`
  (`DashboardStats.totalTenants`) → shown as its own dashboard card.

## Configurable dashboard (new)

Dashboard cards are now individually toggleable: `domain/model/DashboardTile`
enumerates all 11 cards/sections (Properties, Units, Tenants, Occupied,
Vacant, Rent Overview, Total Expenses, Net Income, Recent Payments, Upcoming
Rent, Lease Expiry). `data/preferences/DashboardPreferences.kt` persists the
enabled set via DataStore (nothing stored yet = everything visible, matching
prior behavior). A new **Settings → Customize Dashboard** screen
(`DashboardCustomizationScreen` + `DashboardCustomizationViewModel`) lists
every tile with a switch. `DashboardScreen` now builds its quick-stat grids
dynamically from whichever tiles are enabled — including regrouping enabled
cards two-per-row so there's no gap when some are hidden — and skips
disabled sections entirely.

## Design system & UI redesign (in progress — screen-by-screen)

Following the "theme → navigation → dashboard → property list → ..." order:
worked through the foundation plus the two Priority-1 screens this pass,
verified each compiles cleanly, and stopped there rather than touching all
~100 files at once.

**Foundation (done):**
- `ui/theme/Dimens.kt` — centralized `Spacing` (4/8/12/16/20/24/32dp) and
  `Radius` (8/12/16/20dp) scales. New screens should pull from these instead
  of hardcoding dp values.
- `ui/theme/Type.kt` — a full `AppTypography` scale (headline/title/body/label,
  each with an intentional weight) now wired into `RentManagementTheme`
  instead of the Material3 default `Typography()`.
- Semantic status colors — `SemanticColors` (success/warning/error/info),
  provided via `LocalSemanticColors` so any composable can read
  `LocalSemanticColors.current.success` etc. without prop-drilling. Kept
  separate from the 9 brand palettes so status stays recognizable regardless
  of which app theme is active.
- New reusable components in `ui/components/`: `StatusBadge` (with overloads
  that map `PaymentStatus`/`LeaseStatus`/`UnitStatus` straight to a colored
  pill), `MetricCard` (icon + big value + label), `PrimaryButton` /
  `SecondaryButton` / `DangerButton` (with a built-in loading state on
  `PrimaryButton`), `AppSearchBar`, `FilterChipRow`. `EmptyState` gained an
  optional `icon` parameter (added as a trailing param, so every existing call
  site kept working unchanged).

**Screens redesigned (done):**
- **Dashboard** — greeting header ("Good Morning/Afternoon/Evening"), a 2×2
  metric grid (Properties/Units/Occupied/Vacant), circular icon quick-actions,
  a Rent Overview card (Expected/Collected/Pending/Overdue), Total
  Expenses/Net Income metrics (Net Income's color flips red/green on sign),
  then Recent Payments / Upcoming Rent (next 7 days) / Lease Expiry (next 30
  days) lists — all backed by real data (`DashboardViewModel` now also pulls
  from `PaymentRepository`, `RentRepository`, and `LeaseRepository`).
- **Properties list** — each property is now a card with its icon, name,
  location, "N Units • M Occupied", and an occupancy progress bar with a
  percentage — instead of a bare text list. `PropertyViewModel` now also
  exposes `units` so the list can compute per-property occupancy.

**Property Details / Units (done):** overview stats (Total Units, Occupied,
Vacant, total Monthly Rent) above a redesigned unit list — each unit is now a
card with its status badge, rent, and current tenant name when occupied.
`UnitViewModel` now also exposes the property and tenant list needed for this.

**Tenants / Tenant Details (done):** tenant cards now show their current
unit, phone (with a call icon), a live payment-status badge, and their next
rent due date — computed by combining active leases + all rent records
client-side in `TenantViewModel` (`TenantSummary`), no new DAO queries
needed. Tapping a tenant now opens a **new Tenant Details screen**
(`TenantDetailScreen` + `TenantDetailViewModel`) instead of jumping straight
into the edit form: an avatar header with a tap-to-call button, an Occupancy
card (property/unit/rent/lease end), a Contact Information card, and full
Rent History — with an Edit icon in the top bar for the actual edit form.

**Rent (done):** month stepper (‹ 2026-08 ›), a summary card
(Expected/Collected/Pending/Overdue), a search bar (by tenant name), and
status filter chips (All/Paid/Partially Paid/Pending/Overdue) — all backed
by new `RentViewModel` state (`filteredRentRecords`, `monthSummary`).
Rent cards now use the shared `StatusBadge` component and a clearer
Payable/Paid/Remaining three-column layout.

**Not yet redesigned** (still on the previous functional UI — nothing
broken, just not restyled yet): Record Payment (functionally complete with
PDF receipt sharing from Phase 2, just not visually restyled), Payment
History, Lease Details, Expenses, Reports, Documents, Backup & Restore.
Next up: Record Payment → Lease Details, per the brief's own priority
order.

## Navigation

Replaced the old 5-tab bottom bar + "More" screen with:
- **Bottom bar (4 tabs):** Dashboard, Properties, Rent, Payments — one-tap access
  to the highest-frequency screens.
- **Left slide-in drawer** (Material 3 `ModalNavigationDrawer`), opened via a
  hamburger icon in the top-left of those 4 screens. Chosen over a right-side
  panel because a left drawer is the established Android pattern for primary
  navigation (right panels read as contextual/filter UI, not a main menu).
  Contains everything: the 4 primary destinations, then Tenants / Leases /
  Expenses / Reports / Documents, then Users (admin-only), then Settings /
  Backup & Restore, then Log Out. Shows the signed-in user's name and role at
  the top. Selecting an item closes the drawer and navigates in one motion.

`ui/navigation/AppDrawerContent.kt` holds the drawer's contents;
`ui/navigation/NavGraph.kt`'s `AppScaffold` wires the `DrawerState` and passes
`onMenuClick` into the 4 primary screens.

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
