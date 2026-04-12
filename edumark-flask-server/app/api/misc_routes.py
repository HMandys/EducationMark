from flask import Blueprint

from app.core.result import ok


misc_bp = Blueprint("misc", __name__)


@misc_bp.get("/health")
def health():
    return ok({"status": "UP", "service": "edumark-flask-server"})


@misc_bp.get("/health/ping")
def ping():
    return ok("pong")
