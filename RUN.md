codex
Here’s a concise checklist to run and verify everything built so far. You can start with mock data (no Cal.com) and then switch to live Cal.com.

Prereqs

- Node 18+ and pnpm installed.
- Two terminal tabs: one for Next.js app, one for Astro landing.

Env Setup

- Create apps/web/.env.local (Next.js reads env from its app folder) and set:
    - NEXTAUTH_SECRET=... (any strong random string)
    - NEXTAUTH_URL=http://localhost:3000
    - For mock-only testing, omit Cal.com vars.
    - For live Cal.com testing, set:
    - `CALCOM_BASE_URL=https://api.cal.com/v2`
    - `CALCOM_API_KEY=your_calcom_api_key`
- Ignore the root .env.local; prefer apps/web/.env.local.

DB Setup (Prisma)

- In apps/web:
    - pnpm i
    - npx prisma generate
    - npx prisma migrate dev -n init
- Create test users (via Prisma Studio):
    - npx prisma studio
    - Add a System Admin:
    - users: `name=Admin`, `email=admin@example.com`, `role=SYSTEM_ADMIN`, `password=<bcrypt-hash>`
    - Generate a bcrypt hash locally: `node -e "console.log(require('bcryptjs').hashSync('admin123', 10))"`
- Add a Doctor:
    - users: `name=Dr Test`, `email=doctor@example.com`, `role=DOCTOR`, `password=<bcrypt-hash>`
    - (Optional live mode) set `calcomUserId` to a real Cal.com user ID.
- (Optional) Add a Clinic and link to admin:
    - clinics: `name=ClinicaX`, `slug=clinicx`, `adminUserId=<Admin user id>`
    - (Live mode) set `calcomTeamId` to your Cal.com team id.

Run Apps

- Next.js app (tab 1):
    - cd apps/web
    - pnpm dev (http://localhost:3000)
- Astro landing (tab 2):
    - cd landing
    - pnpm i
    - pnpm dev (http://localhost:4321)

What to Test (Mock Mode)

- Landing (Astro):
    - http://localhost:4321
    - Verify hero, features, testimonials, SEO tags (view source).
- Public booking flow (no login):
    - http://localhost:3000/clinicx
    - Should list mock doctors.
    - Click a doctor → see availability (mock slots) and booking form.
    - Submit → should redirect to confirmation with mock UID.
- Doctor dashboard:
    - http://localhost:3000/auth/login → sign in as doctor@example.com / your password.
    - http://localhost:3000/dashboard → stats + upcoming list (mock if Cal.com not set).
    - Filters and modal detail should work.
- Clinic admin:
    - Sign in as admin@example.com.
    - http://localhost:3000/clinic-admin → manage doctors (add/edit/delete locally, mock list).
    - Appointments view shows mock consolidated list with filters.
- System admin:
    - http://localhost:3000/admin → overview tiles, pending registrations (empty initially).
    - http://localhost:3000/admin/settings → edit config (persists to DB).
    - http://localhost:3000/admin/audit → view audit log (empty until approval actions).

What to Test (Live Cal.com Mode)

- Ensure apps/web/.env.local has valid CALCOM_* and restart the Next.js app.
- Approvals flow (creates real Cal.com resources):
    - Doctor: http://localhost:3000/auth/register → submit; then login as system admin, open http://localhost:3000/admin and approve. This creates Cal.com user and stores calcomUserId.
    - Clinic: http://localhost:3000/auth/register/clinic → submit; approve in admin → creates Cal.com team and stores calcomTeamId.
- Doctor dashboard:
    - Log in as the approved doctor and visit http://localhost:3000/dashboard.
    - Should fetch real bookings from Cal.com for the next 7 days.
- Clinic admin:
    - Log in as the approved clinic admin.
    - http://localhost:3000/clinic-admin:
    - Doctors list → shows real Cal.com team members.
    - Appointments → aggregates real bookings across members (next 7 days).
- Public booking:
    - http://localhost:3000// → availability and booking creation will call Cal.com.
    - On submit, check Cal.com dashboard and your connected calendar for the new event.

Troubleshooting

- If public booking shows no doctors:
    - In mock mode: it should show mocked doctors by default.
    - In live mode: confirm CALCOM_* values and that the clinic slug matches your Cal.com team slug.
- If dashboards show empty lists in live mode:
    - Verify the user’s calcomUserId (doctor) or clinic’s calcomTeamId (admin) are present in the DB (Prisma Studio).
- If login fails:
    - Ensure the stored password is a bcrypt hash and matches what you’re typing.

Want me to seed a default SYSTEM_ADMIN and DOCTOR via a small script to streamline testing, or proceed to Task 10 (notifications and calendar sync verification scaffolding)?
