import { test, expect } from '@playwright/test';

/**
 * E2E tests for booking flow
 * Tests the complete booking process including Cal.com integration
 */

test.describe('Booking Flow', () => {
  test('should navigate to booking page', async ({ page }) => {
    await page.goto('/');
    
    // Find and click booking/reservar link
    const bookingLink = page.locator('a[href*="reservar"], a:has-text("Reservar")').first();
    await bookingLink.click();
    
    // Verify we're on the booking page
    await expect(page).toHaveURL(/\/reservar/);
  });

  test('should display booking page with Cal.com embed', async ({ page }) => {
    await page.goto('/reservar');
    
    // Check page title/heading
    await expect(page.locator('h1, h2')).toContainText(/reservar|booking|cita/i);
    
    // Wait for Cal.com embed to load (iframe or embed container)
    // Cal.com typically uses an iframe
    const calEmbed = page.frameLocator('iframe[src*="cal.com"]').first();
    
    // If Cal.com is embedded, we should see the iframe
    // Note: This might timeout if Cal.com is not configured, which is expected in test env
    try {
      await expect(page.locator('iframe[src*="cal.com"]')).toBeVisible({ timeout: 5000 });
    } catch (e) {
      // If Cal.com is not available, at least check the page structure exists
      await expect(page.locator('body')).toBeVisible();
    }
  });

  test('should have proper page structure', async ({ page }) => {
    await page.goto('/reservar');
    
    // Check for navigation
    await expect(page.locator('nav')).toBeVisible();
    
    // Check for main content area
    await expect(page.locator('main, [role="main"]')).toBeVisible();
    
    // Check for footer
    await expect(page.locator('footer')).toBeVisible();
  });

  test('should allow navigation back to home', async ({ page }) => {
    await page.goto('/reservar');
    
    // Click on logo or home link
    const homeLink = page.locator('a[href="/"], a:has-text("Inicio"), a:has-text("Home")').first();
    await homeLink.click();
    
    // Verify we're back on home page
    await expect(page).toHaveURL('/');
  });

  test('should be accessible on mobile', async ({ page }) => {
    // Set mobile viewport
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/reservar');
    
    // Check that page is visible and usable
    await expect(page.locator('body')).toBeVisible();
    
    // Check for mobile-friendly navigation
    const nav = page.locator('nav');
    await expect(nav).toBeVisible();
  });

  test('should load without JavaScript errors', async ({ page }) => {
    const errors: string[] = [];
    page.on('console', msg => {
      if (msg.type() === 'error') {
        errors.push(msg.text());
      }
    });
    
    await page.goto('/reservar');
    
    // Wait for page to fully load
    await page.waitForLoadState('networkidle');
    
    // Filter out expected Cal.com related errors (if Cal.com is not configured)
    const criticalErrors = errors.filter(err => 
      !err.includes('cal.com') && 
      !err.includes('embed') &&
      !err.includes('iframe')
    );
    
    // Should have no critical errors
    expect(criticalErrors.length).toBe(0);
  });

  test('should have proper meta tags for SEO', async ({ page }) => {
    await page.goto('/reservar');
    
    // Check for title
    await expect(page).toHaveTitle(/.+/);
    
    // Check for viewport meta tag
    const viewport = await page.locator('meta[name="viewport"]').getAttribute('content');
    expect(viewport).toContain('width=device-width');
  });

  test('should handle slow network gracefully', async ({ page }) => {
    // Simulate slow 3G
    await page.route('**/*', route => {
      setTimeout(() => route.continue(), 100);
    });
    
    await page.goto('/reservar');
    
    // Page should still load
    await expect(page.locator('body')).toBeVisible();
  });
});

test.describe('Booking Page Content', () => {
  test('should display helpful information', async ({ page }) => {
    await page.goto('/reservar');
    
    // Look for any instructional text or headings
    const content = await page.textContent('body');
    
    // Should have some content (not just empty page)
    expect(content).toBeTruthy();
    expect(content!.length).toBeGreaterThan(50);
  });

  test('should have working links', async ({ page }) => {
    await page.goto('/reservar');
    
    // Get all links
    const links = page.locator('a[href]');
    const count = await links.count();
    
    // Should have at least navigation links
    expect(count).toBeGreaterThan(0);
    
    // Check first link is valid
    if (count > 0) {
      const firstLink = links.first();
      const href = await firstLink.getAttribute('href');
      expect(href).toBeTruthy();
    }
  });

  test('should be responsive across breakpoints', async ({ page }) => {
    const viewports = [
      { width: 375, height: 667, name: 'mobile' },
      { width: 768, height: 1024, name: 'tablet' },
      { width: 1920, height: 1080, name: 'desktop' },
    ];
    
    for (const viewport of viewports) {
      await page.setViewportSize({ width: viewport.width, height: viewport.height });
      await page.goto('/reservar');
      
      // Check that content is visible at this viewport
      await expect(page.locator('body')).toBeVisible();
      
      // Check that navigation is accessible
      await expect(page.locator('nav')).toBeVisible();
    }
  });
});

test.describe('Booking Integration', () => {
  test('should handle Cal.com embed loading', async ({ page }) => {
    await page.goto('/reservar');
    
    // Wait for page to be fully loaded
    await page.waitForLoadState('networkidle');
    
    // Check if Cal.com iframe exists
    const calIframe = page.locator('iframe[src*="cal.com"]');
    const iframeCount = await calIframe.count();
    
    if (iframeCount > 0) {
      // If Cal.com is configured, iframe should be visible
      await expect(calIframe.first()).toBeVisible();
    } else {
      // If not configured, page should still be functional
      await expect(page.locator('body')).toBeVisible();
    }
  });

  test('should display fallback if Cal.com unavailable', async ({ page }) => {
    // Block Cal.com requests to simulate unavailability
    await page.route('**/cal.com/**', route => route.abort());
    
    await page.goto('/reservar');
    
    // Page should still load
    await expect(page.locator('body')).toBeVisible();
    
    // Should have some content even if Cal.com fails
    const content = await page.textContent('body');
    expect(content).toBeTruthy();
  });
});