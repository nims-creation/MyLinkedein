# MyLinkedin frontend (initial implementation)

React + Vite frontend based on the uploaded MyLinkedin backend controllers.

## Run

1. Ensure Spring Cloud Gateway is running on `http://localhost:8080`.
2. Install Node.js 20+.
3. `npm install`
4. `npm run dev`
5. Open the URL printed by Vite (usually `http://localhost:5173`).

The Vite development proxy forwards `/api` to the gateway. To change its target, start Vite with the `API_GATEWAY_URL` environment variable set.

## Implemented

- Login and signup, using actual backend DTO fields.
- JWT bearer token forwarded to protected services (stored in sessionStorage for this development prototype; production should use a hardened auth/session design).
- Create post and view posts for authenticated user.
- View first-degree connections and request a connection by user ID.
- Responsive UI.

## Known gaps / required backend work

- Gateway routes currently cover users, posts, and connections, but not uploader or notification services.
- No global feed endpoint, profile management, member search, or notification REST API was found in the inspected controllers.
- Authenticated user ID is decoded from the JWT subject client-side only for UI routing; the backend must independently verify the JWT.
- Production CORS, TLS, session strategy, error handling, integration tests, and deployment still need work.
- Do not claim the frontend is tested until `npm install`, `npm run build`, and end-to-end API testing succeed.
