from __future__ import annotations

from functools import wraps
from typing import Any
from uuid import uuid4

from flask import g, request

from app.core.result import fail
from app.core.store import store


def issue_token(user: dict[str, Any]) -> str:
    token = f"token-{uuid4().hex}"
    store.tokens[token] = {
        "userId": user["id"],
        "username": user["username"],
        "roleCode": user.get("roleCode"),
        "roleName": user.get("roleName"),
        "schoolId": user.get("schoolId"),
    }
    return token


def get_token_payload() -> dict[str, Any] | None:
    auth_header = request.headers.get("Authorization", "")
    if not auth_header.startswith("Bearer "):
        return None
    token = auth_header.replace("Bearer ", "", 1).strip()
    payload = store.tokens.get(token)
    if payload:
        user = store.find_by_id("users", payload["userId"])
        g.current_user = user
        g.current_token = token
    return payload


def login_required(fn):
    @wraps(fn)
    def wrapper(*args, **kwargs):
        payload = get_token_payload()
        if not payload:
            return fail("未登录或登录已过期", 401)
        return fn(*args, **kwargs)

    return wrapper


def current_user() -> dict[str, Any] | None:
    return getattr(g, "current_user", None)


def logout_current():
    token = getattr(g, "current_token", None)
    if token and token in store.tokens:
        del store.tokens[token]
