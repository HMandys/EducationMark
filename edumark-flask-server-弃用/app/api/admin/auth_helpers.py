from __future__ import annotations

from flask import request

from app.core.auth import current_user, issue_token, login_required, logout_current
from app.core.result import fail, ok, page_result
from app.core.store import store


def _json():
    return request.get_json(silent=True) or {}


__all__ = [
    "current_user",
    "issue_token",
    "login_required",
    "logout_current",
    "fail",
    "ok",
    "page_result",
    "store",
    "_json",
]
