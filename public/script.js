document.addEventListener('DOMContentLoaded', () => {
  const navToggle = document.getElementById('navToggle');
  const primaryNav = document.getElementById('primaryNav');

  if (!navToggle || !primaryNav) return;

  // Accessible mobile navigation toggle
  navToggle.addEventListener('click', () => {
    const isExpanded = navToggle.getAttribute('aria-expanded') === 'true';
    const nextState = !isExpanded;

    navToggle.setAttribute('aria-expanded', String(nextState));
    primaryNav.classList.toggle('is-active', nextState);

    // Prevent body scroll behind drawer when opened on small screens
    document.body.style.overflow = nextState ? 'hidden' : '';
  });

  // Close nav drawer on click outside
  document.addEventListener('click', (event) => {
    if (
      primaryNav.classList.contains('is-active') &&
      !primaryNav.contains(event.target) &&
      !navToggle.contains(event.target)
    ) {
      primaryNav.classList.remove('is-active');
      navToggle.setAttribute('aria-expanded', 'false');
      document.body.style.overflow = '';
    }
  });

  // Accessible keyboard dismissal via ESC key
  window.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && primaryNav.classList.contains('is-active')) {
      primaryNav.classList.remove('is-active');
      navToggle.setAttribute('aria-expanded', 'false');
      document.body.style.overflow = '';
      navToggle.focus();
    }
  });

  // Automatically reset body overflow if viewport is resized to desktop width
  const mediaQuery768 = window.matchMedia('(min-width: 768px)');
  mediaQuery768.addEventListener('change', (e) => {
    if (e.matches && primaryNav.classList.contains('is-active')) {
      primaryNav.classList.remove('is-active');
      navToggle.setAttribute('aria-expanded', 'false');
      document.body.style.overflow = '';
    }
  });
});
