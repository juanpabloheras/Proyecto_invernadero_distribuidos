const admin = require('../config/firebase-admin');

async function verificarTokenFirebase(req, res, next) {
    try{
        const authHeader = req.headers.authorization;

        if (!authHeader || !authHeader.startsWith('Bearer: ')) {
            return res.status(401).json({
                message: 'Token de autorización'
            })
        }

        const token = authHeader.split(' ')[1];

        const decodedToken = await admin.auth().verifyIdToken(token);

        req.firebaseUser = decodedToken;

        next();
    } catch (error) {
        return res.status(401).json({
            message: 'Token inválido o expirado'
        });
    }
}

module.exports = verificarTokenFirebase;