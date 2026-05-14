export default function LoginButton() {
  const handleLogin = () => {
    window.location.href = "https://salesforce-validation-production.up.railway.app/login";
  };

  return (
    <button onClick={handleLogin}>
      Login with Salesforce
    </button>
  );
}
