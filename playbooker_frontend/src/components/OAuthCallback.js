import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { saveToken } from '../utils/tokenUtil';

export default function OAuthCallback() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const getAccessAndRefreshToken = async () => {
      const code = searchParams.get('code');
      const resp = await fetch(`http://localhost:8080/users/handle-oauth-callback?code=${code}`, {
        credentials: 'include'
      });
      const data = await resp.json();
      if (resp.ok) {
        saveToken(data);
        navigate("/home");
      } else {
        console.log("Authentication Failed");
      }
    };

    getAccessAndRefreshToken();
  }, [searchParams, navigate]);

  return <div>Authorizing...</div>;
}
