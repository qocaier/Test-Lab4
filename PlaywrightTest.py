import pytest
from playwright.sync_api import sync_playwright


cdsvyatkaURL = "https://m.cdsvyatka.com"


@pytest.fixture
def browser_page():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=False)
        page = browser.new_page()
        page.goto(cdsvyatkaURL)
        yield page

def test_task4_scene1(browser_page):
    page = browser_page
    page.locator("#id_MarshSelect").select_option(label="Авт 21: Ипподром-с.Порошино")
    page.locator("#id_buttonMap").click()
    any_bus = page.locator(".leaflet-canvas-icon").first
    any_bus.wait_for(state="visible", timeout=10000)
    any_bus.click()
    popup = page.locator(".leaflet-popup-content")
    text = popup.inner_text()
    assert "Модель" in text, "Ошибка: характеристики автобуса не показываются!"

def test_task4_scene2(browser_page):
    page = browser_page
    page.locator("#id_MarshSelect").select_option(label="Авт 21: Ипподром-с.Порошино")
    page.locator("#id_StopSelect").select_option(label="Село Порошино (код 492)")
    page.locator("#id_buttonPrediction").click()
    table = page.locator("table")
    text = table.inner_text()
    assert "Гос. номер" in text and "Через" in text, "Ошибка: прогноз не показывается!"

def test_task4_scene3(browser_page):
    page = browser_page
    page.locator("#id_MarshSelect").select_option(label="Авт 21: Ипподром-с.Порошино")
    page.locator("#id_StopSelect").select_option(label="Село Порошино (код 492)")
    page.locator("#id_addRaspisToBookMarks_Btn").click()
    page.locator("#id_MarshSelect").select_option(label="Авт 117: Киров-Сидоровка")
    page.locator("#id_StopSelect").select_option(label="Село Порошино (код 492)")
    page.locator("#id_addRaspisToBookMarks_Btn").click()
    page.locator("#id_MarshSelect").select_option(label="Авт 121: Киров-Баташи")
    page.locator("#id_StopSelect").select_option(label="Село Порошино (код 492)")
    page.locator("#id_addRaspisToBookMarks_Btn").click()
    favorites = page.locator("#div_favorits")
    text = favorites.inner_text()
    assert (
        "Авт 21: Ипподром-с.Порошино через Село Порошино (код 492)" in text and
        "Авт 117: Киров-Сидоровка через Село Порошино (код 492)" in text and
        "Авт 121: Киров-Баташи через Село Порошино (код 492)" in text
    ), "Ошибка: избранное не показывается!"