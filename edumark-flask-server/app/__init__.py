from flask import Flask
from werkzeug.exceptions import HTTPException
try:
    from flask_cors import CORS
except ModuleNotFoundError:  # pragma: no cover
    def CORS(app: Flask, **kwargs):
        return app

from app.api.admin_routes import admin_bp
from app.api.app_routes import app_bp
from app.api.misc_routes import misc_bp
from app.core.store import store


def create_app() -> Flask:
    app = Flask(__name__)
    app.config["JSON_AS_ASCII"] = False
    app.config["SECRET_KEY"] = "edumark-flask-secret"

    CORS(app, supports_credentials=True)

    store.bootstrap()

    @app.errorhandler(HTTPException)
    def handle_http_exception(error: HTTPException):
        return {
            "code": error.code or 500,
            "message": error.description or "请求失败",
            "data": None,
            "timestamp": __import__("time").time_ns() // 1_000_000,
        }, 200

    @app.errorhandler(Exception)
    def handle_exception(error: Exception):
        return {
            "code": 500,
            "message": str(error) or "服务器内部错误",
            "data": None,
            "timestamp": __import__("time").time_ns() // 1_000_000,
        }, 200

    app.register_blueprint(admin_bp, url_prefix="/api")
    app.register_blueprint(app_bp, url_prefix="/api")
    app.register_blueprint(misc_bp, url_prefix="/api")
    return app
