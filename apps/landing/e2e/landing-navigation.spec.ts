import { test, expect } from '@playwright/test';

/**
 * E2E tests for landing page navigation
 * Tests navigation between different pages and responsive design
 */

test.describe('Landing Page Navigation', () => {
  test('should load the home page successfully', async ({ page }) => {
    await page.goto('/');
    
    // Check that the page loaded
    await expect(page).toHaveTitle(/CitaMedica/i);
    
    // Check for main navigation elements
    await expect(page.locator('nav')).toBeVisible();
  });

  test('should navigate to pricing page', async ({ page }) => {
    await page.goto('/');
    
    // Click on pricing link
    await page.click('text=/precios|pricing/i');
    
    // Verify we're on the pricing page
    await expect(page).toHaveURL(/\/precios/);
    await expect(page.locator('h1, h2')).toContainText(/precio|plan/i);
  });

  test('should navigate to contact page', async ({ page }) => {
    await page.goto('/');
    
    // Click on contact link
    await page.click('text=/contacto|contact/i');
    
    // Verify we're on the contact page
    await expect(page).toHaveURL(/\/contacto/);
    await expect(page.locator('h1, h2')).toContainText(/contacto|contact/i);
  });

  test('should navigate to blog page', async ({ page }) => {
    await page.goto('/');
    
    // Click on blog link
    await page.click('text=/blog/i');
    
    // Verify we're on the blog page
    await expect(page).toHaveURL(/\/blog/);
  });

  test('should have working CTA buttons', async ({ page }) => {
    await page.goto('/');
    
    // Look for CTA buttons (Reservar demo, Comenzar, etc.)
    const ctaButton = page.locator('a[href*="reservar"], button:has-text("Reservar"), a:has-text("Comenzar")').first();
    await expect(ctaButton).toBeVisible();
  });

  test('should display footer with links', async ({ page }) => {
    await page.goto('/');
    
    // Scroll to footer
    await page.evaluate(() => window.scrollTo(0, document.body.scrollHeight));
    
    // Check footer exists
    const footer = page.locator('footer');
    await expect(footer).toBeVisible();
  });

  test('should handle 404 page', async ({ page }) => {
    await page.goto('/non-existent-page');
    
    // Should show 404 or redirect
    const content = await page.textContent('body');
    expect(content).toMatch(/404|not found|no encontrado/i);
  });

  test('should have accessible navigation', async ({ page }) => {
    await page.goto('/');
    
    // Check for proper heading hierarchy
    const h1 = page.locator('h1');
    await expect(h1).toHaveCount(1);
    
    // Check for skip links or proper ARIA labels
    const nav = page.locator('nav');
    await expect(nav).toBeVisible();
  });
});

test.describe('Responsive Design', () => {
  test('should be responsive on mobile', async ({ page }) => {
    // Set mobile viewport
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/');
    
    // Check that content is visible
    await expect(page.locator('body')).toBeVisible();
    
    // Check for mobile menu (hamburger icon)
    const mobileMenu = page.locator('button[aria-label*="menu"], button:has-text("☰")');
    if (await mobileMenu.count() > 0) {
      await expect(mobileMenu.first()).toBeVisible();
    }
  });

  test('should be responsive on tablet', async ({ page }) => {
    // Set tablet viewport
    await page.setViewportSize({ width: 768, height: 1024 });
    await page.goto('/');
    
    // Check that content is visible and properly laid out
    await expect(page.locator('body')).toBeVisible();
    await expect(page.locator('nav')).toBeVisible();
  });

  test('should be responsive on desktop', async ({ page }) => {
    // Set desktop viewport
    await page.setViewportSize({ width: 1920, height: 1080 });
    await page.goto('/');
    
    // Check that content is visible
    await expect(page.locator('body')).toBeVisible();
    await expect(page.locator('nav')).toBeVisible();
  });

  test('should handle viewport changes', async ({ page }) => {
    await page.goto('/');
    
    // Start with desktop
    await page.setViewportSize({ width: 1920, height: 1080 });
    await expect(page.locator('body')).toBeVisible();
    
    // Switch to mobile
    await page.setViewportSize({ width: 375, height: 667 });
    await expect(page.locator('body')).toBeVisible();
    
    // Content should still be accessible
    await expect(page.locator('nav')).toBeVisible();
  });
});

test.describe('Performance and Accessibility', () => {
  test('should load within acceptable time', async ({ page }) => {
    const startTime = Date.now();
    await page.goto('/');
    const loadTime = Date.now() - startTime;
    
    // Should load in less than 5 seconds
    expect(loadTime).toBeLessThan(5000);
  });

  test('should have proper meta tags', async ({ page }) => {
    await page.goto('/');
    
    // Check for viewport meta tag
    const viewport = await page.locator('meta[name="viewport"]').getAttribute('content');
    expect(viewport).toContain('width=device-width');
  });

  test('should have no console errors on load', async ({ page }) => {
    const errors: string[] = [];
    page.on('console', msg => {
      if (msg.type() === 'error') {
        errors.push(msg.text());
      }
    });
    
    await page.goto('/');
    
    // Allow some time for any async errors
    await page.waitForTimeout(2000);
    
    // Should have no critical errors
    expect(errors.length).toBe(0);
  });
});