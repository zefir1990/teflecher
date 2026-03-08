import http.server
import socketserver
import sys
import os

PORT = 8000
os.chdir(os.path.join(sys.path[0], 'src', 'composeApp', 'build', 'dist', 'wasmJs', 'productionExecutable'))

Handler = http.server.SimpleHTTPRequestHandler
Handler.extensions_map['.wasm'] = 'application/wasm'
Handler.extensions_map['.mjs'] = 'application/javascript'
Handler.extensions_map['.js'] = 'application/javascript'

with socketserver.TCPServer(('', PORT), Handler) as httpd:
    print('Serving at port', PORT)
    httpd.serve_forever()