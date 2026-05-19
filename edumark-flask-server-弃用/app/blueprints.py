from app.api.admin_routes import admin_bp
from app.api.app_routes import app_bp
from app.api.misc_routes import misc_bp


def register_blueprints(app, url_prefix: str):
    app.register_blueprint(admin_bp, url_prefix=url_prefix)
    app.register_blueprint(app_bp, url_prefix=url_prefix)
    app.register_blueprint(misc_bp, url_prefix=url_prefix)
