import { useEffect, useState } from 'react';
import { Portal } from './portal';

type PortalRoute = 'login' | 'workspace';

function readPortalRoute(): PortalRoute {
  return window.location.hash === '#workspace' ? 'workspace' : 'login';
}

function App() {
  const [route, setRoute] = useState<PortalRoute>(() => readPortalRoute());

  useEffect(() => {
    const syncRoute = () => setRoute(readPortalRoute());
    window.addEventListener('hashchange', syncRoute);
    return () => window.removeEventListener('hashchange', syncRoute);
  }, []);

  function navigate(nextRoute: PortalRoute) {
    setRoute(nextRoute);
    window.location.hash = nextRoute;
  }

  return (
    <Portal
      route={route}
      onHome={() => navigate('login')}
      onRouteChange={navigate}
    />
  );
}

export default App;
