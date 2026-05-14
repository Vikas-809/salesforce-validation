import { useEffect, useState } from "react";
import { getRules, toggleRule, toggleAll } from "../api/salesforceApi";

export default function Dashboard() {

  const [rules, setRules] = useState([]);
  const [user, setUser] = useState(null);

  useEffect(() => {
    fetch("http://localhost:5000/api/salesforce/me", {
      credentials: "include"
    })
      .then(res => res.ok ? res.json() : null)
      .then(data => setUser(data?.username || null));
  }, []);

  const fetchRules = async () => {
    const data = await getRules();
    setRules(data.records || []);
  };

  const logout = async () => {
    await fetch("http://localhost:5000/api/salesforce/logout", {
      credentials: "include"
    });
    setUser(null);
    setRules([]);
  };

  return (
    <div>
      <h1>Salesforce Validation Rules</h1>

      {!user ? (
        <a href="http://localhost:5000/login">
          <button>Login</button>
        </a>
      ) : (
        <>
          <h3>Welcome {user}</h3>
          <button onClick={logout}>Logout</button>

          <br /><br />

          <button onClick={fetchRules}>Get Rules</button>

          <br /><br />

          <button onClick={() => { toggleAll(true); fetchRules(); }}>
            Enable All
          </button>

          <button onClick={() => { toggleAll(false); fetchRules(); }}>
            Disable All
          </button>

          <br /><br />

          {rules.map(r => (
            <div key={r.Id}>
              {r.ValidationName} | {r.Active ? "ON" : "OFF"}
              <button onClick={() => toggleRule(r.Id, !r.Active).then(fetchRules)}>
                {r.Active ? "Disable" : "Enable"}
              </button>
            </div>
          ))}
        </>
      )}
    </div>
  );
}