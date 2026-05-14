import { useEffect, useState } from "react";
import { getRules, toggleRule, toggleAll } from "../api/salesforceApi";
import "../App.css";

export default function RuleList() {
  const [rules, setRules] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchRules = async () => {
    setLoading(true);
    const data = await getRules();
    setRules(data.records || []);
    setLoading(false);
  };

  useEffect(() => { fetchRules(); }, []);

  const handleToggle = async (id, current) => {
    await toggleRule(id, !current);
    fetchRules();
  };

  const handleToggleAll = async (active) => {
    await toggleAll(active);
    fetchRules();
  };

  return (
    <div style={{ marginTop: 16 }}>
      <div className="actions">
        <button className="btn btn-success" onClick={() => handleToggleAll(true)}>Enable All</button>
        <button className="btn btn-danger" onClick={() => handleToggleAll(false)}>Disable All</button>
        <button className="btn btn-outline" onClick={fetchRules}>Refresh</button>
      </div>

      {loading && <div className="muted">Loading…</div>}

      {!loading && rules.map(rule => (
        <div className="row" key={rule.Id}>
          <div>
            <div>{rule.ValidationName}</div>
            <div className={`badge ${rule.Active ? "badge-on" : "badge-off"}`}>
              {rule.Active ? "Active" : "Inactive"}
            </div>
          </div>

          <button
            className={`btn ${rule.Active ? "btn-danger" : "btn-success"}`}
            onClick={() => handleToggle(rule.Id, rule.Active)}
          >
            {rule.Active ? "Disable" : "Enable"}
          </button>
        </div>
      ))}
    </div>
  );
}