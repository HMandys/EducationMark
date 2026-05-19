from flask import Flask
from werkzeug.exceptions import HTTPException

try:
    from flask_cors import CORS
except ModuleNotFoundError:  # pragma: no cover
    def CORS(app: Flask, **kwargs):
        return app

from app.blueprints import register_blueprints
from app.config import Config
from app.core.store import store


def _error_payload(code: int, message: str):
    return {
        "code": code,
        "message": message,
        "data": None,
        "timestamp": __import__("time").time_ns() // 1_000_000,
    }, 200


def create_app(config_object: type[Config] = Config) -> Flask:
    app = Flask(__name__)
    app.config.from_object(config_object)

    CORS(app, supports_credentials=app.config["CORS_SUPPORTS_CREDENTIALS"])
    store.bootstrap()

    @app.errorhandler(HTTPException)
    def handle_http_exception(error: HTTPException):
        return _error_payload(error.code or 500, error.description or "请求失败")

    @app.errorhandler(Exception)
    def handle_exception(error: Exception):
        return _error_payload(500, str(error) or "服务器内部错误")

    register_blueprints(app, app.config["API_PREFIX"])
    return app
