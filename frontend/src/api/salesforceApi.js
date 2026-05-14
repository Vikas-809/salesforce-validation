const BASE_URL = "https://salesforce-validation-production.up.railway.app/api/salesforce";


export const getRules = async () => {
  const res = await fetch(`${BASE_URL}/validation-rules`, {
    credentials: "include"
  });
  return res.json();
};

export const toggleRule = async (id, active) => {
  await fetch(`${BASE_URL}/toggle/${id}?active=${active}`, {
    method: "PATCH",
    credentials: "include"
  });
};

export const toggleAll = async (active) => {
  await fetch(`${BASE_URL}/toggle-all?active=${active}`, {
    method: "PATCH",
    credentials: "include"
  });
};
