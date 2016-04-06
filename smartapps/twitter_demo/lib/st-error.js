function STError(message, other) {
    Error.captureStackTrace(this, this.constructor);
    this.name = this.constructor.name;
    this.message = message;
    this.other = other;
};

module.exports = STError;
