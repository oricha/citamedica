
import NextAuth, { NextAuthOptions } from 'next-auth';
import CredentialsProvider from 'next-auth/providers/credentials';
import { PrismaClient } from '@prisma/client';
import bcrypt from 'bcryptjs';

const prisma = new PrismaClient();

export const authOptions: NextAuthOptions = {
  providers: [
    CredentialsProvider({
      name: 'Credentials',
      credentials: {
        email: { label: "Email", type: "text" },
        password: {  label: "Password", type: "password" }
      },
      async authorize(credentials) {
        if (!credentials?.email || !credentials.password) {
          return null;
        }

        const user = await prisma.user.findUnique({
          where: { email: credentials.email }
        });

        if (user && user.password && await bcrypt.compare(credentials.password, user.password)) {
          return { id: user.id, name: user.name, email: user.email, role: user.role } as any;
        } else {
          return null;
        }
      }
    })
  ],
  secret: process.env.NEXTAUTH_SECRET,
  session: {
    strategy: 'jwt'
  },
  callbacks: {
    async jwt({ token, user }) {
      if (user) {
        token.id = user.id;
        // @ts-ignore
        token.role = user.role;
        // enrich token with Cal.com IDs
        try {
          const dbUser = await prisma.user.findUnique({ where: { id: String(user.id) }, include: { clinicsAsAdmin: true } });
          // @ts-ignore
          token.calcomUserId = dbUser?.calcomUserId || null;
          // If clinic admin, take first clinic's team id as context
          const clinicTeamId = dbUser?.clinicsAsAdmin?.[0]?.calcomTeamId || null;
          // @ts-ignore
          token.clinicTeamId = clinicTeamId;
        } catch (e) {
          // ignore enrichment failures
        }
      }
      return token;
    },
    async session({ session, token }) {
      if (session.user) {
        // @ts-ignore
        session.user.id = token.id;
        // @ts-ignore
        session.user.role = token.role;
        // @ts-ignore
        session.user.calcomUserId = token.calcomUserId || null;
        // @ts-ignore
        session.user.clinicTeamId = token.clinicTeamId || null;
      }
      return session;
    }
  },
  pages: {
    signIn: '/auth/login',
  }
};

const handler = NextAuth(authOptions);

export { handler as GET, handler as POST };
