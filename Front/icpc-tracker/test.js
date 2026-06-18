import puppeteer from 'playwright';
(async () => {
  const browser = await puppeteer.chromium.launch();
  const page = await browser.newPage();
  await page.goto('http://localhost:5173/dashboard/trainee');
  
  const html = await page.evaluate(() => {
    return document.body.innerHTML;
  });
  console.log(html);
  await browser.close();
})();
