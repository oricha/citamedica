# E2E Tests - CitaMedica Landing

This directory contains end-to-end tests for the CitaMedica landing page using Playwright.

## Setup

Install dependencies:

```bash
npm install
```

Install Playwright browsers:

```bash
npx playwright install
```

## Running Tests

### Run all tests
```bash
npm run test:e2e
```

### Run tests in UI mode (interactive)
```bash
npm run test:e2e:ui
```

### Run tests in headed mode (see browser)
```bash
npm run test:e2e:headed
```

### Run specific test file
```bash
npx playwright test landing-navigation.spec.ts
```

### Run tests on specific browser
```bash
npx playwright test --project=chromium
npx playwright test --project=firefox
npx playwright test --project=webkit
```

## Test Structure

### `landing-navigation.spec.ts`
Tests for landing page navigation and responsive design:
- Home page loading
- Navigation between pages (pricing, contact, blog)
- CTA buttons functionality
- Footer and accessibility
- Responsive design across mobile, tablet, and desktop
- Performance and meta tags

### `booking-flow.spec.ts`
Tests for the booking flow:
- Navigation to booking page
- Cal.com embed integration
- Page structure and accessibility
- Mobile responsiveness
- Error handling and fallbacks
- SEO meta tags

## Test Reports

After running tests, view the HTML report:

```bash
npx playwright show-report
```

## CI/CD Integration

Tests are configured to run in CI with:
- Automatic retries (2 retries on failure)
- Single worker for stability
- Screenshot and trace on failure

## Writing New Tests

Follow the existing patterns:
1. Use descriptive test names
2. Group related tests with `test.describe()`
3. Test both happy paths and error cases
4. Include responsive design tests
5. Add accessibility checks where appropriate

## Troubleshooting

### Tests timing out
- Increase timeout in `playwright.config.ts`
- Check if dev server is running properly
- Verify network connectivity

### Cal.com embed not loading
- This is expected in test environment if Cal.com is not configured
- Tests include fallback checks for this scenario

### Browser not installed
Run: `npx playwright install`

## Best Practices

1. **Keep tests independent**: Each test should be able to run in isolation
2. **Use data-testid**: Add `data-testid` attributes for stable selectors
3. **Avoid hardcoded waits**: Use Playwright's auto-waiting features
4. **Test user flows**: Focus on real user scenarios
5. **Check accessibility**: Include ARIA labels and semantic HTML checks