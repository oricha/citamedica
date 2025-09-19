
import { withAuth } from "next-auth/middleware"
import { NextResponse } from "next/server"

export default withAuth(
  function middleware(req) {
    const { token } = req.nextauth
    const { pathname } = req.nextUrl

    if (pathname.startsWith('/dashboard') && !token) {
      return NextResponse.redirect(new URL('/auth/login', req.url))
    }

    if (pathname.startsWith('/admin') && token?.role !== 'SYSTEM_ADMIN') {
      return NextResponse.redirect(new URL('/unauthorized', req.url))
    }

    if (pathname.startsWith('/clinic-admin') && token?.role !== 'CLINIC_ADMIN') {
        return NextResponse.redirect(new URL('/unauthorized', req.url))
    }

    return NextResponse.next()
  },
  {
    callbacks: {
      authorized: ({ token }) => !!token,
    },
  }
)

export const config = {
  matcher: ['/dashboard/:path*', '/admin/:path*', '/clinic-admin/:path*'],
}
