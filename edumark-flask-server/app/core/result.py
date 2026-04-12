from __future__ import annotations

from typing import Any

from flask import jsonify


def ok(data: Any = None, message: str = "操作成功", code: int = 200):
    return jsonify({
        "code": code,
        "message": message,
        "data": data,
        "timestamp": __import__("time").time_ns() // 1_000_000,
    })


def page_result(items: list[dict], total: int, page_num: int, page_size: int):
    return ok({
        "list": items,
        "total": total,
        "pageNum": page_num,
        "pageSize": page_size,
        "pages": (total + page_size - 1) // page_size if page_size else 0,
    })


def fail(message: str = "请求失败", code: int = 500):
    return jsonify({
        "code": code,
        "message": message,
        "data": None,
        "timestamp": __import__("time").time_ns() // 1_000_000,
    }), 200
