import http.server
import socketserver

PORT = 8000
Handler = http.server.SimpleHTTPRequestHandler
Handler.extensions_map['.wasm'] = 'application/wasm'

print("Is .wasm present?", '.wasm' in Handler.extensions_map)
print("Type:", Handler.extensions_map['.wasm'])
