import { useEffect, useState } from "react";
import { getRules, toggleRule, toggleAll } from "../api/salesforceApi";

const BACKEND_URL =
  "https://salesforce-validation-production.up.railway.app";

export default function Dashboard() {

  const [rules, setRules] = useState([]);
  const [user, setUser] = useState(null);

  // =========================
  // CHECK LOGGED IN USER
  // =========================
  useEffect(() => {
    fetch(`${BACKEND_URL}/api/salesforce/me`, {
      credentials: "include"
    })
      .then(res => res.ok ? res.json() : null)
      .then(data => setUser(data?.username || null))
      .catch(() => setUser(null));
  }, []);

  // =========================
  // FETCH RULES
  // =========================
  const fetchRules = async () => {
    try {
      const data = await getRules();
      setRules(data.records || []);
    } catch (error) {
      console.error(error);
    }
  };

  // =========================
  // LOGOUT
  // =========================
  const logout = async () => {
    await fetch(`${BACKEND_URL}/api/salesforce/logout`, {
      credentials: "include"
    });

    setUser(null);
    setRules([]);
  };

  return (
    <div style={{ textAlign: "center", marginTop: "40px" }}>
      <h1>Salesforce Validation Rules</h1>

      {!user ? (
        <a href={`${BACKEND_URL}/login`}>
          <button>Login</button>
        </a>
      ) : (
        <>
          <h3>Welcome {user}</h3>

          <button onClick={logout}>
            Logout
          </button>

          <br /><br />

          <button onClick={fetchRules}>
            Get Rules
          </button>

          <br /><br />

          <button
            onClick={async () => {
              await toggleAll(true);
              fetchRules();
            }}
          >
            Enable All
          </button>

          <button
            onClick={async () => {
              await toggleAll(false);
              fetchRules();
            }}
          >
            Disable All
          </button>

          <br /><br />

          {rules.map((r) => (
            <div
              key={r.Id}
              style={{ marginBottom: "10px" }}
            >
              {r.ValidationName} |{" "}
              {r.Active ? "ON" : "OFF"}

              <button
                style={{ marginLeft: "10px" }}
                onClick={() =>
                  toggleRule(r.Id, !r.Active)
                    .then(fetchRules)
                }
              >
                {r.Active ? "Disable" : "Enable"}
              </button>
            </div>
          ))}
        </>
      )}
    </div>
  );
}
